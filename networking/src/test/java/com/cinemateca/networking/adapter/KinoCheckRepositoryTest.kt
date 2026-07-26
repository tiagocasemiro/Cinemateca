package com.cinemateca.networking.adapter

import com.cinemateca.domain.Failure
import com.cinemateca.domain.Success
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.trailers.model.ContentLanguage
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerGenre
import com.cinemateca.domain.trailers.model.VideoCategory
import com.cinemateca.networking.gateway.KinoCheckGateway
import com.cinemateca.networking.response.TrailerPageResponse
import com.cinemateca.networking.response.TrailerPageResponseDeserializer
import com.google.gson.GsonBuilder
import java.util.concurrent.TimeUnit
import kotlinx.coroutines.test.runTest
import okhttp3.mockwebserver.MockResponse
import okhttp3.mockwebserver.MockWebServer
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class KinoCheckRepositoryTest {
    private lateinit var server: MockWebServer
    private lateinit var trailerRepository: TrailerRemoteImpl
    private lateinit var movieRepository: MovieRemoteImpl

    @Before
    fun setUp() {
        server = MockWebServer()
        server.start()
        val gson = GsonBuilder()
            .registerTypeAdapter(
                TrailerPageResponse::class.java,
                TrailerPageResponseDeserializer(),
            )
            .create()
        val gateway = Retrofit.Builder()
            .baseUrl(server.url("/"))
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
            .create(KinoCheckGateway::class.java)

        trailerRepository = TrailerRemoteImpl(gateway)
        movieRepository = MovieRemoteImpl(gateway)
    }

    @After
    fun tearDown() {
        server.shutdown()
    }

    @Test
    fun `filtered trailers map dynamic items and pagination`() = runTest {
        server.enqueue(jsonResponse(TRAILER_PAGE_JSON))

        val result = trailerRepository.getTrailers(
            TrailerFilters(
                genres = linkedSetOf(TrailerGenre.ACTION, TrailerGenre.DRAMA),
                categories = linkedSetOf(VideoCategory.TRAILER, VideoCategory.CLIP),
                language = ContentLanguage.ENGLISH,
                page = 2,
                limit = 10,
            )
        )

        assertTrue(result is Success)
        val page = (result as Success).data
        assertEquals("trailer-1", page.trailers.single().id)
        assertEquals(299534, page.trailers.single().resource?.tmdbId)
        assertEquals(4, page.metadata.totalPages)
        assertEquals(38, page.metadata.totalCount)

        val requestUrl = requireNotNull(server.takeRequest().requestUrl)
        assertEquals("/trailers", requestUrl.encodedPath)
        assertEquals("Action,Drama", requestUrl.queryParameter("genres"))
        assertEquals("Trailer,Clip", requestUrl.queryParameter("categories"))
        assertEquals("en", requestUrl.queryParameter("language"))
        assertEquals("2", requestUrl.queryParameter("page"))
        assertEquals("10", requestUrl.queryParameter("limit"))
    }

    @Test
    fun `movie details support KinoCheck TMDB and IMDB identifiers`() = runTest {
        repeat(3) {
            server.enqueue(jsonResponse(MOVIE_JSON))
        }
        val filters = MovieVideoFilters(
            categories = linkedSetOf(VideoCategory.TRAILER, VideoCategory.FEATURETTE),
            language = ContentLanguage.GERMAN,
        )

        val kinoCheckResult = movieRepository.getByKinoCheckId("ly4", filters)
        val tmdbResult = movieRepository.getByTmdbId(299534, filters)
        val imdbResult = movieRepository.getByImdbId("tt4154796", filters)

        assertEquals("Avengers: Endgame", (kinoCheckResult as Success).data.title)
        assertEquals("video-1", (tmdbResult as Success).data.videos.single().id)
        assertEquals("recommended-1", (imdbResult as Success).data.recommendations.single().id)

        val kinoCheckUrl = requireNotNull(server.takeRequest().requestUrl)
        val tmdbUrl = requireNotNull(server.takeRequest().requestUrl)
        val imdbUrl = requireNotNull(server.takeRequest().requestUrl)
        assertEquals("ly4", kinoCheckUrl.queryParameter("id"))
        assertEquals(null, kinoCheckUrl.queryParameter("tmdb_id"))
        assertEquals("299534", tmdbUrl.queryParameter("tmdb_id"))
        assertEquals(null, tmdbUrl.queryParameter("imdb_id"))
        assertEquals("tt4154796", imdbUrl.queryParameter("imdb_id"))
        assertEquals(null, imdbUrl.queryParameter("id"))
        assertEquals("Trailer,Featurette", imdbUrl.queryParameter("categories"))
        assertEquals("de", imdbUrl.queryParameter("language"))
    }

    @Test
    fun `KinoCheck show resource uses the shows endpoint`() = runTest {
        server.enqueue(jsonResponse(MOVIE_JSON))

        val result = movieRepository.getByKinoCheckId(
            id = "show-1",
            resourceType = MediaResourceType.Show,
        )

        assertTrue(result is Success)
        val requestUrl = requireNotNull(server.takeRequest().requestUrl)
        assertEquals("/shows", requestUrl.encodedPath)
        assertEquals("show-1", requestUrl.queryParameter("id"))
    }

    @Test
    fun `http errors become domain failures`() = runTest {
        server.enqueue(
            MockResponse()
                .setResponseCode(404)
                .setHeader("Content-Type", "application/json")
                .setBody("""{"code":404,"title":"Not found","message":"Unknown movie"}""")
        )

        val result = movieRepository.getByImdbId("tt0000000")

        assertTrue(result is Failure)
        val error = (result as Failure).error
        assertEquals(404, error?.httpError)
        assertEquals("Unknown movie", error?.message)
    }

    private fun jsonResponse(body: String) = MockResponse()
        .setResponseCode(200)
        .setHeader("Content-Type", "application/json")
        .setBody(body)
        .setBodyDelay(1, TimeUnit.MILLISECONDS)

    private companion object {
        val TRAILER_PAGE_JSON = """
            {
              "0": {
                "id": "trailer-1",
                "youtube_video_id": "youtube-1",
                "youtube_channel_id": "channel-1",
                "youtube_thumbnail": "https://img.youtube.com/1",
                "title": "Trailer title",
                "url": "https://kinocheck.com/trailer/1",
                "thumbnail": "https://cdn.kinocheck.com/1.jpg",
                "language": "en",
                "categories": ["Trailer"],
                "genres": ["Action", "Drama"],
                "published": "2026-07-25T10:00:00+02:00",
                "views": 42,
                "resource": {
                  "type": "movie",
                  "path": "/movies/",
                  "id": "ly4",
                  "imdb_id": "tt4154796",
                  "tmdb_id": 299534
                }
              },
              "_metadata": {
                "limit": 10,
                "page": 2,
                "total_pages": 4,
                "total_count": 38
              }
            }
        """.trimIndent()

        val MOVIE_JSON = """
            {
              "id": "ly4",
              "tmdb_id": 299534,
              "imdb_id": "tt4154796",
              "language": "en",
              "title": "Avengers: Endgame",
              "url": "https://kinocheck.com/movie/ly4",
              "trailer": {
                "id": "trailer-1",
                "title": "Official trailer",
                "categories": ["Trailer"],
                "genres": ["Action"],
                "views": 100
              },
              "videos": [{
                "id": "video-1",
                "title": "Official video",
                "categories": ["Featurette"],
                "genres": ["Action"],
                "views": 50
              }],
              "recommendations": [{
                "id": "recommended-1",
                "tmdb_id": 1,
                "imdb_id": "tt0000001",
                "language": "en",
                "title": "Recommended movie",
                "url": "https://kinocheck.com/movie/recommended-1"
              }]
            }
        """.trimIndent()
    }
}
