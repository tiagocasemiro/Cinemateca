package com.cinemateca.features.trailers.home

import com.cinemateca.domain.Error
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Result
import com.cinemateca.domain.Success
import com.cinemateca.domain.trailers.model.PageMetadata
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
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

    @Test
    fun `starts with a complete renderable state`() {
        coEvery { getTrendingTrailersUseCase(any()) } returns Success(trailerPage())

        val viewModel = HomeViewModel(getTrendingTrailersUseCase)

        assertEquals(HomeUiState(), viewModel.uiState.value)
    }

    @Test
    fun `loads trending trailers during initialization`() = runTest {
        val pendingResult = CompletableDeferred<Result<TrailerPage>>()
        coEvery { getTrendingTrailersUseCase(any()) } coAnswers { pendingResult.await() }
        val viewModel = HomeViewModel(getTrendingTrailersUseCase)

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
        val viewModel = HomeViewModel(getTrendingTrailersUseCase)
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
        val viewModel = HomeViewModel(getTrendingTrailersUseCase)
        runCurrent()

        viewModel.onAction(HomeUiAction.Refresh)
        runCurrent()

        coVerify(exactly = 1) { getTrendingTrailersUseCase(any()) }
        pendingResult.complete(Success(trailerPage()))
        advanceUntilIdle()
    }

    private fun trailerPage() = TrailerPage(
        trailers = listOf(trailer()),
        metadata = PageMetadata(
            limit = 25,
            page = 1,
            totalPages = 1,
            totalCount = 1,
        ),
    )

    private fun trailer() = Trailer(
        id = "trailer-1",
        youtubeVideoId = "youtube-1",
        youtubeChannelId = "channel-1",
        youtubeThumbnail = "https://img.youtube.com/1",
        title = "Trailer em alta",
        url = "https://kinocheck.com/trailer/1",
        thumbnail = "https://cdn.kinocheck.com/1.jpg",
        language = "pt",
        categories = listOf("Trailer"),
        genres = listOf("Action"),
        published = "2026-07-25T10:00:00-03:00",
        views = 42,
        resource = null,
    )
}
