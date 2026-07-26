package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MovieSelectionUseCasesTest {
    @Test
    fun `observes and toggles favorite movie ids`() = runTest {
        val repository = FakeFavoriteMovieRepository(
            initialIds = setOf("movie-1"),
        )
        val observeUseCase = ObserveFavoriteMovieIdsUseCase(repository)
        val toggleUseCase = ToggleFavoriteMovieUseCase(repository)

        assertEquals(setOf("movie-1"), observeUseCase().first())

        toggleUseCase(
            movieId = "movie-1",
            isCurrentlySelected = true,
        )
        assertFalse("movie-1" in observeUseCase().first())

        toggleUseCase(
            movieId = "movie-2",
            isCurrentlySelected = false,
        )
        assertTrue("movie-2" in observeUseCase().first())
    }

    @Test
    fun `observes and toggles watchlist movie ids`() = runTest {
        val repository = FakeWatchlistMovieRepository(
            initialIds = setOf("movie-1"),
        )
        val observeUseCase = ObserveWatchlistMovieIdsUseCase(repository)
        val toggleUseCase = ToggleWatchlistMovieUseCase(repository)

        assertEquals(setOf("movie-1"), observeUseCase().first())

        toggleUseCase(
            movieId = "movie-2",
            isCurrentlySelected = false,
        )

        assertEquals(
            setOf("movie-1", "movie-2"),
            observeUseCase().first(),
        )
    }
}

private class FakeFavoriteMovieRepository(
    initialIds: Set<String>,
) : FavoriteMovieRepository.Local {
    private val movieIds = MutableStateFlow(initialIds)

    override fun observeMovieIds(): Flow<Set<String>> = movieIds

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) {
        movieIds.value = movieIds.value.withSelection(
            movieId = movieId,
            isSelected = isSelected,
        )
    }
}

private class FakeWatchlistMovieRepository(
    initialIds: Set<String>,
) : WatchlistMovieRepository.Local {
    private val movieIds = MutableStateFlow(initialIds)

    override fun observeMovieIds(): Flow<Set<String>> = movieIds

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) {
        movieIds.value = movieIds.value.withSelection(
            movieId = movieId,
            isSelected = isSelected,
        )
    }
}

private fun Set<String>.withSelection(
    movieId: String,
    isSelected: Boolean,
): Set<String> {
    return if (isSelected) this + movieId else this - movieId
}
