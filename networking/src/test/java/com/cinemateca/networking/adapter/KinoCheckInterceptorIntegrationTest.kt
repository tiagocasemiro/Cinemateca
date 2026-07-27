package com.cinemateca.networking.adapter

import com.cinemateca.domain.Failure
import com.cinemateca.domain.Success
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.trailers.model.ContentLanguage
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerGenre
import com.cinemateca.domain.trailers.model.VideoCategory
import com.cinemateca.networking.di.kinoCheckNetworkingModule
import com.cinemateca.networking.gateway.KinoCheckGateway
import com.cinemateca.networking.interceptor.ExpectedRequest
import com.cinemateca.networking.interceptor.FixtureResponseInterceptor
import com.cinemateca.networking.interceptor.InconsistentRequestException
import kotlinx.coroutines.test.runTest
import okhttp3.OkHttpClient
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.koin.core.KoinApplication
import org.koin.dsl.koinApplication
import retrofit2.Retrofit

class KinoCheckInterceptorIntegrationTest {
    private lateinit var koinApplication: KoinApplication
    private lateinit var fixtureInterceptor: FixtureResponseInterceptor
    private lateinit var originalRetrofit: Retrofit
    private lateinit var testRetrofit: Retrofit
    private lateinit var testClient: OkHttpClient
    private lateinit var gateway: KinoCheckGateway
    private lateinit var trailerRepository: TrailerRemoteImpl
    private lateinit var movieRepository: MovieRemoteImpl

    @Before
    fun setUp() {
        koinApplication = koinApplication {
            modules(kinoCheckNetworkingModule(apiKey = API_KEY))
        }

        originalRetrofit = koinApplication.koin.get()
        val originalClient = koinApplication.koin.get<OkHttpClient>()
        fixtureInterceptor = FixtureResponseInterceptor(::loadFixture)
        testClient = originalClient.newBuilder()
            .addInterceptor(fixtureInterceptor)
            .build()
        testRetrofit = originalRetrofit.newBuilder()
            .client(testClient)
            .build()

        gateway = testRetrofit.create(KinoCheckGateway::class.java)
        trailerRepository = TrailerRemoteImpl(gateway)
        movieRepository = MovieRemoteImpl(gateway)
    }

    @After
    fun tearDown() {
        testClient.dispatcher.executorService.shutdown()
        testClient.connectionPool.evictAll()
        koinApplication.close()
    }

    @Test
    fun `fixture interceptor is the last interceptor of the replaced Retrofit`() {
        assertSame(fixtureInterceptor, testClient.interceptors.last())
        assertEquals(originalRetrofit.baseUrl(), testRetrofit.baseUrl())
        assertEquals(
            originalRetrofit.converterFactories().size,
            testRetrofit.converterFactories().size,
        )
    }

    @Test
    fun `filtered trailer request is validated deserialized and mapped`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "trailer_page.json",
                expectedRequest = expectedRequest(
                    path = "/trailers",
                    query = mapOf(
                        "genres" to "Action,Drama",
                        "categories" to "Trailer,Clip",
                        "language" to "en",
                        "page" to "2",
                        "limit" to "10",
                    ),
                ),
            )

            val result = trailerRepository.getTrailers(
                TrailerFilters(
                    genres = linkedSetOf(TrailerGenre.ACTION, TrailerGenre.DRAMA),
                    categories = linkedSetOf(VideoCategory.TRAILER, VideoCategory.CLIP),
                    language = ContentLanguage.ENGLISH,
                    page = 2,
                    limit = 10,
                ),
            )

            assertTrue(result is Success)
            val page = (result as Success).data
            assertEquals(listOf("trailer-1", "trailer-2"), page.trailers.map { it.id })
            assertEquals("youtube-1", page.trailers.first().youtubeVideoId)
            assertEquals(299534, page.trailers.first().resource?.tmdbId)
            assertEquals(2, page.metadata.page)
            assertEquals(4, page.metadata.totalPages)
            assertEquals(38, page.metadata.totalCount)
            assertEquals(1, fixtureInterceptor.interceptedRequestCount)
        }

    @Test
    fun `trending and latest serialize default pagination without optional filters`() =
        runTest {
            val defaultQuery = mapOf("page" to "1", "limit" to "25")
            fixtureInterceptor.respondWith(
                fixture = "trailer_page.json",
                expectedRequest = expectedRequest(
                    path = "/trailers/trending",
                    query = defaultQuery,
                ),
            )

            val trending = trailerRepository.getTrending()

            fixtureInterceptor.respondWith(
                fixture = "trailer_page.json",
                expectedRequest = expectedRequest(
                    path = "/trailers/latest",
                    query = defaultQuery,
                ),
            )
            val latest = trailerRepository.getLatest()

            assertTrue(trending is Success)
            assertTrue(latest is Success)
            assertEquals(2, fixtureInterceptor.interceptedRequestCount)
        }

    @Test
    fun `movie identifiers are serialized exclusively and response is mapped`() =
        runTest {
            val filters = MovieVideoFilters(
                categories = linkedSetOf(
                    VideoCategory.TRAILER,
                    VideoCategory.FEATURETTE,
                ),
                language = ContentLanguage.GERMAN,
            )
            fixtureInterceptor.respondWith(
                fixture = "movie.json",
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf(
                        "id" to "ly4",
                        "categories" to "Trailer,Featurette",
                        "language" to "de",
                    ),
                ),
            )
            val kinoCheckResult = movieRepository.getByKinoCheckId("ly4", filters)

            fixtureInterceptor.respondWith(
                fixture = "movie.json",
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf(
                        "tmdb_id" to "299534",
                        "categories" to "Trailer,Featurette",
                        "language" to "de",
                    ),
                ),
            )
            val tmdbResult = movieRepository.getByTmdbId(299534, filters)

            fixtureInterceptor.respondWith(
                fixture = "movie.json",
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf(
                        "imdb_id" to "tt4154796",
                        "categories" to "Trailer,Featurette",
                        "language" to "de",
                    ),
                ),
            )
            val imdbResult = movieRepository.getByImdbId("tt4154796", filters)

            assertEquals("Avengers: Endgame", (kinoCheckResult as Success).data.title)
            assertEquals("video-1", (tmdbResult as Success).data.videos.single().id)
            assertEquals(
                "recommended-1",
                (imdbResult as Success).data.recommendations.single().id,
            )
        }

    @Test
    fun `show resource is serialized to shows endpoint`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "movie.json",
                expectedRequest = expectedRequest(
                    path = "/shows",
                    query = mapOf("id" to "show-1"),
                ),
            )

            val result = movieRepository.getByKinoCheckId(
                id = "show-1",
                resourceType = MediaResourceType.Show,
            )

            assertTrue(result is Success)
            assertEquals("ly4", (result as Success).data.id)
        }

    @Test
    fun `nullable response fields are converted to safe domain defaults`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "sparse_movie.json",
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf("id" to "sparse"),
                ),
            )

            val result = movieRepository.getByKinoCheckId("sparse")

            val movie = (result as Success).data
            assertEquals("", movie.id)
            assertEquals("", movie.title)
            assertEquals(emptyList<Any>(), movie.videos)
            assertEquals(emptyList<Any>(), movie.recommendations)
        }

    @Test
    fun `remote HTTP error fixture is converted to domain failure`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "api_error.json",
                statusCode = 404,
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf("imdb_id" to "tt0000000"),
                ),
            )

            val result = movieRepository.getByImdbId("tt0000000")

            assertTrue(result is Failure)
            val error = (result as Failure).error
            assertEquals(404, error?.code)
            assertEquals(404, error?.httpError)
            assertEquals("Not found", error?.title)
            assertEquals("Unknown movie", error?.message)
        }

    @Test
    fun `malformed JSON is converted to unexpected domain failure`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "malformed.json",
                expectedRequest = expectedRequest(
                    path = "/movies",
                    query = mapOf("id" to "broken"),
                ),
            )

            val result = movieRepository.getByKinoCheckId("broken")

            assertTrue(result is Failure)
            val error = (result as Failure).error
            assertEquals(266, error?.code)
            assertEquals("", error?.formattedTitle)
            assertEquals("", error?.formattedMessage)
        }

    @Test
    fun `inconsistent request throws with every invalid element before fixture response`() =
        runTest {
            fixtureInterceptor.respondWith(
                fixture = "trailer_page.json",
                expectedRequest = expectedRequest(
                    path = "/wrong-path",
                    query = mapOf(
                        "page" to "99",
                        "missing" to "value",
                    ),
                ),
            )

            val failure = runCatching {
                gateway.getLatest(
                    genres = null,
                    categories = null,
                    language = null,
                    page = 1,
                    limit = 25,
                )
            }.exceptionOrNull()

            assertTrue(failure is InconsistentRequestException)
            val inconsistencies = (failure as InconsistentRequestException).inconsistencies
            assertTrue(inconsistencies.any { it.startsWith("path expected") })
            assertTrue(inconsistencies.any { it.startsWith("missing query parameters") })
            assertTrue(inconsistencies.any { it.startsWith("unexpected query parameters") })
            assertTrue(inconsistencies.any { it.startsWith("query <page>") })
            assertFalse(inconsistencies.isEmpty())
        }

    private fun expectedRequest(
        path: String,
        query: Map<String, String>,
    ) = ExpectedRequest(
        encodedPath = path,
        query = query,
        headers = mapOf(
            "Accept" to "application/json",
            "X-Api-Key" to API_KEY,
            "X-Api-Host" to API_HOST,
        ),
    )

    private fun loadFixture(name: String): String {
        val resource = checkNotNull(javaClass.classLoader.getResource("fixtures/$name")) {
            "Fixture not found: $name"
        }
        return resource.readText()
    }

    private companion object {
        const val API_KEY = "test-api-key"
        const val API_HOST = "api.kinocheck.com"
    }
}
