package com.cinemateca.features.trailers.home

import com.cinemateca.domain.Error
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Result
import com.cinemateca.domain.Success
import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.trailers.model.PageMetadata
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import java.time.OffsetDateTime
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getTrendingTrailersUseCase = mockk<GetTrendingTrailersUseCase>()
    private val internetAvailability = MutableStateFlow(true)
    private val observeInternetConnectionUseCase =
        ObserveInternetConnectionUseCase(
            repository = FakeInternetConnectionRepository(
                availability = internetAvailability,
            ),
        )

    @Test
    fun `starts with a complete renderable state`() {
        coEvery { getTrendingTrailersUseCase(any()) } returns Success(trailerPage())

        val viewModel = createViewModel()

        assertEquals(HomeUiState(), viewModel.uiState.value)
    }

    @Test
    fun `loads trending trailers during initialization`() = runTest {
        val pendingResult = CompletableDeferred<Result<TrailerPage>>()
        coEvery { getTrendingTrailersUseCase(any()) } coAnswers { pendingResult.await() }
        val viewModel = createViewModel()

        runCurrent()

        assertTrue(viewModel.uiState.value.isLoading)
        assertTrue(viewModel.uiState.value.trailers.isEmpty())

        pendingResult.complete(Success(trailerPage()))
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals(
            listOf(
                HomeTrailerItemUiModel(
                    id = "trailer-1",
                    title = "Trailer em alta",
                    thumbnailUrl = "https://cdn.kinocheck.com/1.jpg",
                    genres = "Action",
                    published = "25 jul. 2026",
                ),
            ),
            viewModel.uiState.value.trailers,
        )
        assertNull(viewModel.uiState.value.errorMessage)
    }

    @Test
    fun `shows domain failure and retries the request`() = runTest {
        var result: Result<TrailerPage> = Failure(
            Error(message = "Serviço indisponível"),
        )
        coEvery { getTrendingTrailersUseCase(any()) } coAnswers { result }
        val viewModel = createViewModel()
        advanceUntilIdle()

        assertFalse(viewModel.uiState.value.isLoading)
        assertEquals("Serviço indisponível", viewModel.uiState.value.errorMessage)

        result = Success(trailerPage())
        viewModel.onAction(HomeUiAction.Retry)
        advanceUntilIdle()

        assertEquals("trailer-1", viewModel.uiState.value.trailers.single().id)
        assertNull(viewModel.uiState.value.errorMessage)
        coVerify(exactly = 2) { getTrendingTrailersUseCase(any()) }
    }

    @Test
    fun `ignores refresh while a request is active`() = runTest {
        val pendingResult = CompletableDeferred<Result<TrailerPage>>()
        coEvery { getTrendingTrailersUseCase(any()) } coAnswers { pendingResult.await() }
        val viewModel = createViewModel()
        runCurrent()

        viewModel.onAction(HomeUiAction.Refresh)
        runCurrent()

        coVerify(exactly = 1) { getTrendingTrailersUseCase(any()) }
        pendingResult.complete(Success(trailerPage()))
        advanceUntilIdle()
    }

    @Test
    fun `shows offline state without requesting trailers when app opens offline`() =
        runTest {
            internetAvailability.value = false
            val viewModel = createViewModel()

            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isOffline)
            assertFalse(viewModel.uiState.value.isLoading)
            assertNull(viewModel.uiState.value.errorMessage)
            coVerify(exactly = 0) { getTrendingTrailersUseCase(any()) }
        }

    @Test
    fun `detects internet loss and reloads after validated connection returns`() =
        runTest {
            coEvery { getTrendingTrailersUseCase(any()) } returns Success(
                trailerPage(),
            )
            val viewModel = createViewModel()
            advanceUntilIdle()

            internetAvailability.value = false
            advanceUntilIdle()

            assertTrue(viewModel.uiState.value.isOffline)

            internetAvailability.value = true
            advanceUntilIdle()

            assertFalse(viewModel.uiState.value.isOffline)
            coVerify(exactly = 2) { getTrendingTrailersUseCase(any()) }
        }

    @Test
    fun `sorts loaded trailers and exposes the selected sort label`() =
        runTest {
            coEvery { getTrendingTrailersUseCase(any()) } returns Success(
                trailerPage(
                    trailers = listOf(
                        trailer(
                            id = "bravo",
                            title = "Bravo",
                            published = "2026-07-20T10:00:00-03:00",
                            views = 10,
                        ),
                        trailer(
                            id = "charlie",
                            title = "Charlie",
                            published = "2026-07-26T10:00:00-03:00",
                            views = 20,
                        ),
                        trailer(
                            id = "alpha",
                            title = "Alpha",
                            published = "2026-07-25T10:00:00-03:00",
                            views = 30,
                        ),
                    ),
                ),
            )
            val viewModel = createViewModel()
            advanceUntilIdle()

            assertEquals(
                listOf("charlie", "alpha", "bravo"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(
                HomeUiAction.SelectSortOption(
                    HomeSortOption.MostPopular,
                ),
            )

            assertEquals(
                HomeSortOption.MostPopular,
                viewModel.uiState.value.sortOption,
            )
            assertEquals(
                listOf("alpha", "charlie", "bravo"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(
                HomeUiAction.SelectSortOption(
                    HomeSortOption.Alphabetical,
                ),
            )

            assertEquals(
                listOf("alpha", "bravo", "charlie"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )
        }

    @Test
    fun `filters loaded trailers by title as the search query changes`() =
        runTest {
            coEvery { getTrendingTrailersUseCase(any()) } returns Success(
                trailerPage(
                    trailers = listOf(
                        trailer(
                            id = "transformers",
                            title = "Transformers: O Início",
                        ),
                        trailer(
                            id = "deadpool",
                            title = "Deadpool & Wolverine",
                        ),
                        trailer(
                            id = "wicked",
                            title = "Wicked",
                        ),
                    ),
                ),
            )
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(HomeUiAction.SearchQueryChanged("WOL"))

            assertEquals("WOL", viewModel.uiState.value.searchQuery)
            assertEquals(
                listOf("deadpool"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(HomeUiAction.SearchQueryChanged("   "))

            assertEquals(
                listOf("transformers", "deadpool", "wicked"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )
        }

    @Test
    fun `filters loaded trailers by the calculated movie release window`() =
        runTest {
            val now = OffsetDateTime.now()
            coEvery { getTrendingTrailersUseCase(any()) } returns Success(
                trailerPage(
                    trailers = listOf(
                        trailer(
                            id = "released",
                            title = "Zulu",
                            published = now.minusMonths(2).toString(),
                        ),
                        trailer(
                            id = "upcoming",
                            title = "Charlie",
                            published = now.minusDays(10).toString(),
                        ),
                        trailer(
                            id = "now-playing-late",
                            title = "Bravo",
                            published = now
                                .minusMonths(1)
                                .minusDays(20)
                                .toString(),
                        ),
                        trailer(
                            id = "now-playing-early",
                            title = "Alpha",
                            published = now
                                .minusMonths(1)
                                .minusDays(5)
                                .toString(),
                        ),
                        trailer(
                            id = "unknown-date",
                            title = "Data desconhecida",
                            published = "invalid",
                        ),
                    ),
                ),
            )
            val viewModel = createViewModel()
            advanceUntilIdle()

            viewModel.onAction(
                HomeUiAction.SelectSortOption(
                    HomeSortOption.Alphabetical,
                ),
            )
            viewModel.onAction(
                HomeUiAction.SelectFilterOption(
                    HomeFilterOption.NowPlaying,
                ),
            )

            assertEquals(
                HomeFilterOption.NowPlaying,
                viewModel.uiState.value.filterOption,
            )
            assertEquals(
                listOf("now-playing-early", "now-playing-late"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(
                HomeUiAction.SelectFilterOption(
                    HomeFilterOption.Releases,
                ),
            )
            assertEquals(
                listOf("released"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(
                HomeUiAction.SelectFilterOption(
                    HomeFilterOption.Upcoming,
                ),
            )
            assertEquals(
                listOf("upcoming"),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )

            viewModel.onAction(
                HomeUiAction.SelectFilterOption(
                    HomeFilterOption.All,
                ),
            )
            assertEquals(
                listOf(
                    "now-playing-early",
                    "now-playing-late",
                    "upcoming",
                    "unknown-date",
                    "released",
                ),
                viewModel.uiState.value.trailers.map {
                    it.id
                },
            )
        }

    private fun createViewModel() = HomeViewModel(
        getTrendingTrailersUseCase = getTrendingTrailersUseCase,
        observeInternetConnectionUseCase = observeInternetConnectionUseCase,
    )

    private fun trailerPage(
        trailers: List<Trailer> = listOf(trailer()),
    ) = TrailerPage(
        trailers = trailers,
        metadata = PageMetadata(
            limit = 25,
            page = 1,
            totalPages = 1,
            totalCount = 1,
        ),
    )

    private fun trailer(
        id: String = "trailer-1",
        title: String = "Trailer em alta",
        published: String? = "2026-07-25T10:00:00-03:00",
        views: Long? = 42,
        categories: List<String> = listOf("Trailer"),
    ) = Trailer(
        id = id,
        youtubeVideoId = "youtube-1",
        youtubeChannelId = "channel-1",
        youtubeThumbnail = "https://img.youtube.com/1",
        title = title,
        url = "https://kinocheck.com/trailer/1",
        thumbnail = "https://cdn.kinocheck.com/1.jpg",
        language = "pt",
        categories = categories,
        genres = listOf("Action"),
        published = published,
        views = views,
        resource = null,
    )
}

private class FakeInternetConnectionRepository(
    private val availability: Flow<Boolean>,
) : InternetConnectionRepository.Local {
    override fun observeAvailability(): Flow<Boolean> = availability
}
