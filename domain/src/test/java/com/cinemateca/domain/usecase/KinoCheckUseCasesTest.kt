package com.cinemateca.domain.usecase

import com.cinemateca.domain.Error
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Result
import com.cinemateca.domain.Success
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.model.MovieSummary
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.movies.repository.MovieRepository
import com.cinemateca.domain.movies.usecase.GetMovieByImdbIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByTmdbIdUseCase
import com.cinemateca.domain.trailers.model.ContentLanguage
import com.cinemateca.domain.trailers.model.MediaReference
import com.cinemateca.domain.trailers.model.PageMetadata
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerGenre
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.domain.trailers.model.VideoCategory
import com.cinemateca.domain.trailers.repository.TrailerRepository
import com.cinemateca.domain.trailers.usecase.GetLatestTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertSame
import org.junit.Assert.fail
import org.junit.Test

class KinoCheckUseCasesTest {
    @Test
    fun `trailer use cases expose complete repository data and filters`() = runTest {
        val expected = Success(trailerPage())
        val repository = FakeTrailerRepository(result = expected)
        val filters = TrailerFilters(
            genres = setOf(TrailerGenre.ACTION),
            categories = setOf(VideoCategory.TRAILER),
            language = ContentLanguage.ENGLISH,
            page = 2,
            limit = 10,
        )

        assertSame(expected, GetTrendingTrailersUseCase(repository)(filters))
        assertEquals(TrailerOperation.TRENDING, repository.lastOperation)
        assertEquals(filters, repository.lastFilters)

        assertSame(expected, GetLatestTrailersUseCase(repository)(filters))
        assertEquals(TrailerOperation.LATEST, repository.lastOperation)
        assertEquals(filters, repository.lastFilters)

        assertSame(expected, GetTrailersUseCase(repository)(filters))
        assertEquals(TrailerOperation.FILTERED, repository.lastOperation)
        assertEquals(filters, repository.lastFilters)
    }

    @Test
    fun `trailer use cases preserve repository failures`() = runTest {
        val expected = Failure(Error(code = 503, message = "Unavailable"))
        val repository = FakeTrailerRepository(result = expected)

        val result = GetTrendingTrailersUseCase(repository)()

        assertSame(expected, result)
    }

    @Test
    fun `movie use cases expose all details for each identifier`() = runTest {
        val expected = Success(movie())
        val repository = FakeMovieRepository(result = expected)
        val filters = MovieVideoFilters(
            categories = setOf(VideoCategory.TRAILER, VideoCategory.FEATURETTE),
            language = ContentLanguage.GERMAN,
        )

        assertSame(expected, GetMovieByKinoCheckIdUseCase(repository)("kino-1", filters))
        assertEquals(MovieOperation.KINO_CHECK_ID, repository.lastOperation)
        assertEquals("kino-1", repository.lastIdentifier)
        assertEquals(filters, repository.lastFilters)

        assertSame(expected, GetMovieByTmdbIdUseCase(repository)(299534, filters))
        assertEquals(MovieOperation.TMDB_ID, repository.lastOperation)
        assertEquals(299534, repository.lastIdentifier)
        assertEquals(filters, repository.lastFilters)

        assertSame(expected, GetMovieByImdbIdUseCase(repository)("tt4154796", filters))
        assertEquals(MovieOperation.IMDB_ID, repository.lastOperation)
        assertEquals("tt4154796", repository.lastIdentifier)
        assertEquals(filters, repository.lastFilters)
    }

    @Test
    fun `movie use cases reject invalid identifiers before repository access`() = runTest {
        val repository = FakeMovieRepository(result = Success(movie()))

        expectIllegalArgument { GetMovieByKinoCheckIdUseCase(repository)(" ") }
        expectIllegalArgument { GetMovieByTmdbIdUseCase(repository)(0) }
        expectIllegalArgument { GetMovieByImdbIdUseCase(repository)("") }

        assertEquals(0, repository.callCount)
    }

    private suspend fun expectIllegalArgument(block: suspend () -> Unit) {
        try {
            block()
            fail("Expected IllegalArgumentException")
        } catch (_: IllegalArgumentException) {
            Unit
        }
    }

    private fun trailerPage() = TrailerPage(
        trailers = listOf(trailer()),
        metadata = PageMetadata(
            limit = 10,
            page = 2,
            totalPages = 3,
            totalCount = 21,
        ),
    )

    private fun trailer() = Trailer(
        id = "trailer-1",
        youtubeVideoId = "youtube-1",
        youtubeChannelId = "channel-1",
        youtubeThumbnail = "https://img.youtube.com/1",
        title = "Official trailer",
        url = "https://kinocheck.com/trailer/1",
        thumbnail = "https://cdn.kinocheck.com/1.jpg",
        language = "en",
        categories = listOf("Trailer"),
        genres = listOf("Action"),
        published = "2026-07-25T10:00:00+02:00",
        views = 42,
        resource = MediaReference(
            type = "movie",
            path = "/movies/",
            kinoCheckId = "kino-1",
            imdbId = "tt4154796",
            tmdbId = 299534,
        ),
    )

    private fun movie() = Movie(
        id = "kino-1",
        tmdbId = 299534,
        imdbId = "tt4154796",
        language = "en",
        title = "Movie title",
        url = "https://kinocheck.com/movie/1",
        trailer = trailer(),
        videos = listOf(trailer().copy(id = "video-1")),
        recommendations = listOf(
            MovieSummary(
                id = "movie-2",
                tmdbId = 2,
                imdbId = "tt0000002",
                language = "en",
                title = "Recommended movie",
                url = "https://kinocheck.com/movie/2",
            )
        ),
    )
}

private enum class TrailerOperation {
    TRENDING,
    LATEST,
    FILTERED,
}

private class FakeTrailerRepository(
    var result: Result<TrailerPage>,
) : TrailerRepository.Remote {
    var lastOperation: TrailerOperation? = null
    var lastFilters: TrailerFilters? = null

    override suspend fun getTrending(filters: TrailerFilters): Result<TrailerPage> {
        lastOperation = TrailerOperation.TRENDING
        lastFilters = filters
        return result
    }

    override suspend fun getLatest(filters: TrailerFilters): Result<TrailerPage> {
        lastOperation = TrailerOperation.LATEST
        lastFilters = filters
        return result
    }

    override suspend fun getTrailers(filters: TrailerFilters): Result<TrailerPage> {
        lastOperation = TrailerOperation.FILTERED
        lastFilters = filters
        return result
    }
}

private enum class MovieOperation {
    KINO_CHECK_ID,
    TMDB_ID,
    IMDB_ID,
}

private class FakeMovieRepository(
    var result: Result<Movie>,
) : MovieRepository.Remote {
    var lastOperation: MovieOperation? = null
    var lastIdentifier: Any? = null
    var lastFilters: MovieVideoFilters? = null
    var callCount: Int = 0

    override suspend fun getByKinoCheckId(
        id: String,
        filters: MovieVideoFilters,
        resourceType: MediaResourceType,
    ): Result<Movie> = record(MovieOperation.KINO_CHECK_ID, id, filters)

    override suspend fun getByTmdbId(
        tmdbId: Int,
        filters: MovieVideoFilters,
    ): Result<Movie> = record(MovieOperation.TMDB_ID, tmdbId, filters)

    override suspend fun getByImdbId(
        imdbId: String,
        filters: MovieVideoFilters,
    ): Result<Movie> = record(MovieOperation.IMDB_ID, imdbId, filters)

    private fun record(
        operation: MovieOperation,
        identifier: Any,
        filters: MovieVideoFilters,
    ): Result<Movie> {
        callCount += 1
        lastOperation = operation
        lastIdentifier = identifier
        lastFilters = filters
        return result
    }
}
