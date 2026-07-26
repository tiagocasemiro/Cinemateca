package com.cinemateca.features.trailers.details

import androidx.lifecycle.SavedStateHandle
import com.cinemateca.domain.Error
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Success
import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.UiText
import com.cinemateca.features.trailers.home.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(RobolectricTestRunner::class)
class TrailerDetailsViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getMovieUseCase = mockk<GetMovieByKinoCheckIdUseCase>()
    private val favoriteIds = MutableStateFlow<Set<String>>(emptySet())
    private val watchlistIds = MutableStateFlow<Set<String>>(emptySet())
    private val isInternetAvailable = MutableStateFlow(true)
    private val favoriteRepository = FakeFavoriteRepository(favoriteIds)
    private val watchlistRepository = FakeWatchlistRepository(watchlistIds)

    @Test
    fun `loads the movie and selects the clicked trailer`() = runTest {
        coEvery {
            getMovieUseCase("movie-1", any(), any())
        } returns Success(movie())

        val viewModel = createViewModel()
        advanceUntilIdle()

        val state = viewModel.uiState.value
        assertFalse(state.isLoading)
        assertEquals("Movie title", state.details?.title)
        assertEquals("video-2", state.details?.trailerId)
        assertEquals(
            UiText.resource(R.string.compact_millions, "2.5"),
            state.details?.views,
        )
        assertEquals(
            UiText.plural(R.plurals.trailer_count, 2, 2),
            state.details?.videoCount,
        )
        assertEquals(
            UiText.resource(
                R.string.display_date,
                25,
                UiText.Resource(R.string.month_july_short),
                "2026",
            ),
            state.details?.published,
        )
        assertEquals("youtube-2", state.details?.youtubeVideoId)
        assertEquals(
            "video-2",
            state.details?.promotionalVideos?.first()?.id,
        )
    }

    @Test
    fun `observes and toggles persisted movie selections`() = runTest {
        favoriteIds.value = setOf("movie-1")
        coEvery {
            getMovieUseCase("movie-1", any(), any())
        } returns Success(movie())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.details?.isFavorite == true)
        assertFalse(viewModel.uiState.value.details?.isWatchlisted == true)

        viewModel.onAction(TrailerDetailsUiAction.ToggleFavorite)
        viewModel.onAction(TrailerDetailsUiAction.ToggleWatchlist)
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.details?.isFavorite == true)
        assertTrue(viewModel.uiState.value.details?.isWatchlisted == true)
        assertFalse("movie-1" in favoriteIds.value)
        assertTrue("movie-1" in watchlistIds.value)
    }

    @Test
    fun `shows the domain error and supports retry`() = runTest {
        coEvery {
            getMovieUseCase("movie-1", any(), any())
        } returns Failure(Error(message = "Falha no detalhe"))

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertEquals(
            UiText.Resource(R.string.details_default_error),
            viewModel.uiState.value.errorMessage,
        )
    }

    @Test
    fun `shows offline state and loads details when connection returns`() = runTest {
        isInternetAvailable.value = false
        coEvery {
            getMovieUseCase("movie-1", any(), any())
        } returns Success(movie())

        val viewModel = createViewModel()
        advanceUntilIdle()

        assertTrue(viewModel.uiState.value.isOffline)
        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(null, viewModel.uiState.value.details)

        isInternetAvailable.value = true
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isOffline)
        assertEquals("Movie title", viewModel.uiState.value.details?.title)
    }

    private fun createViewModel() = TrailerDetailsViewModel(
        savedStateHandle = SavedStateHandle(
            mapOf(
                "movieId" to "movie-1",
                "trailerId" to "video-2",
                "resourceType" to "movie",
            ),
        ),
        getMovieByKinoCheckIdUseCase = getMovieUseCase,
        observeInternetConnectionUseCase = ObserveInternetConnectionUseCase(
            repository = FakeInternetConnectionRepository(
                availability = isInternetAvailable,
            ),
        ),
        observeFavoriteMovieIdsUseCase = ObserveFavoriteMovieIdsUseCase(
            repository = favoriteRepository,
        ),
        observeWatchlistMovieIdsUseCase = ObserveWatchlistMovieIdsUseCase(
            repository = watchlistRepository,
        ),
        toggleFavoriteMovieUseCase = ToggleFavoriteMovieUseCase(
            repository = favoriteRepository,
        ),
        toggleWatchlistMovieUseCase = ToggleWatchlistMovieUseCase(
            repository = watchlistRepository,
        ),
    )

    private fun movie() = Movie(
        id = "movie-1",
        tmdbId = 1,
        imdbId = "tt1",
        language = "pt",
        title = "Movie title",
        url = "https://kinocheck.com/movie",
        trailer = trailer(id = "video-1", views = 100),
        videos = listOf(
            trailer(id = "video-1", views = 100),
            trailer(
                id = "video-2",
                views = 2_500_000,
                published = "2026-07-25T10:00:00-03:00",
            ),
        ),
        recommendations = emptyList(),
    )

    private fun trailer(
        id: String,
        views: Long,
        published: String = "2026-07-20T10:00:00-03:00",
    ) = Trailer(
        id = id,
        youtubeVideoId = "youtube-${id.takeLast(1)}",
        youtubeChannelId = "channel",
        youtubeThumbnail = "https://img.youtube.com/$id",
        title = "Trailer $id",
        url = "https://kinocheck.com/$id",
        thumbnail = "https://cdn.kinocheck.com/$id.jpg",
        language = "pt",
        categories = listOf("Trailer"),
        genres = listOf("Ação", "Comédia"),
        published = published,
        views = views,
        resource = null,
    )
}

private class FakeInternetConnectionRepository(
    private val availability: MutableStateFlow<Boolean>,
) : InternetConnectionRepository.Local {
    override fun observeAvailability(): Flow<Boolean> = availability
}

private class FakeFavoriteRepository(
    private val ids: MutableStateFlow<Set<String>>,
) : FavoriteMovieRepository.Local {
    override fun observeMovieIds(): Flow<Set<String>> = ids

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) {
        ids.value = if (isSelected) ids.value + movieId else ids.value - movieId
    }
}

private class FakeWatchlistRepository(
    private val ids: MutableStateFlow<Set<String>>,
) : WatchlistMovieRepository.Local {
    override fun observeMovieIds(): Flow<Set<String>> = ids

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) {
        ids.value = if (isSelected) ids.value + movieId else ids.value - movieId
    }
}
