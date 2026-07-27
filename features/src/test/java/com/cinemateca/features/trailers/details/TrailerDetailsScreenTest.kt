package com.cinemateca.features.trailers.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.designsystem.UiText
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class TrailerDetailsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `renders loading skeleton and forwards back click`() {
        var backClicks = 0
        setDetailsContent(
            uiState = TrailerDetailsUiState(),
            onBackClick = { backClicks++ },
        )

        composeRule
            .onNodeWithTag("$DETAILS_ID.loading")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$DETAILS_ID.loading.hero.back")
            .assertIsDisplayed()
            .performClick()

        assertEquals(1, backClicks)
    }

    @Test
    fun `renders the complete trailer details layout`() {
        setDetailsContent(
            uiState = TrailerDetailsUiState(
                isLoading = false,
                details = details(),
            ),
        )

        composeRule
            .onNodeWithTag(DETAILS_ID)
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.hero")
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Deadpool & Wolverine")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.body.description")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.body.youtube")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `renders offline state and forwards its actions`() {
        val callbacks = mutableListOf<String>()
        setDetailsContent(
            uiState = TrailerDetailsUiState(
                isLoading = false,
                isOffline = true,
            ),
            onAction = { callbacks += it.toString() },
            onBackClick = { callbacks += "back" },
        )

        composeRule
            .onNodeWithTag("$DETAILS_ID.offline")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$DETAILS_ID.offline.retry")
            .performClick()
        composeRule
            .onNodeWithTag("$DETAILS_ID.offline.back")
            .performClick()

        assertEquals(
            listOf(TrailerDetailsUiAction.Retry.toString(), "back"),
            callbacks,
        )
    }

    @Test
    fun `forwards header and trailer action clicks`() {
        val callbacks = mutableListOf<String>()
        setDetailsContent(
            uiState = TrailerDetailsUiState(
                isLoading = false,
                details = details().copy(
                    isFavorite = true,
                    isWatchlisted = true,
                ),
            ),
            onAction = { action -> callbacks += action.toString() },
            onBackClick = { callbacks += "back" },
            onShareClick = { callbacks += "share" },
            onYouTubeClick = { callbacks += "youtube" },
            onPromotionalVideoClick = { callbacks += "promo:$it" },
        )

        composeRule
            .onNodeWithTag("$DETAILS_ID.content.hero.back")
            .performClick()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.hero.share")
            .performClick()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.actions.favorite")
            .assertIsSelected()
            .performClick()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.actions.watchlist")
            .assertIsSelected()
            .performClick()
        composeRule
            .onNodeWithTag(
                "$DETAILS_ID.content.body.promotional.video.official",
            )
            .performScrollTo()
            .performClick()
        composeRule
            .onNodeWithTag("$DETAILS_ID.content.body.youtube")
            .performScrollTo()
            .performClick()

        assertEquals(
            listOf(
                "back",
                "share",
                TrailerDetailsUiAction.ToggleFavorite.toString(),
                TrailerDetailsUiAction.ToggleWatchlist.toString(),
                "promo:youtube-official",
                "youtube",
            ),
            callbacks,
        )
    }

    private fun details() = TrailerDetailsUiModel(
        movieId = "deadpool",
        trailerId = "official",
        title = "Deadpool & Wolverine",
        thumbnailUrl = null,
        topBadge = UiText.Dynamic("Trailer"),
        views = UiText.Dynamic("3.1M"),
        videoCount = UiText.Dynamic("12 trailers"),
        published = UiText.Dynamic("25 Jul 2024"),
        tags = listOf("#ação", "#comédia", "#inglês"),
        description = UiText.Dynamic("Descrição de exemplo."),
        promotionalVideos = listOf(
            PromotionalVideoUiModel(
                id = "official",
                title = "Trailer oficial — Deadpool & Wolverine",
                thumbnailUrl = null,
                subtitle = UiText.Dynamic("Trailer"),
                youtubeVideoId = "youtube-official",
            ),
            PromotionalVideoUiModel(
                id = "teaser",
                title = "Teaser — Deadpool & Wolverine",
                thumbnailUrl = null,
                subtitle = UiText.Dynamic("Teaser"),
                youtubeVideoId = "youtube-teaser",
            ),
        ),
        youtubeVideoId = "youtube-id",
        isFavorite = false,
        isWatchlisted = false,
    )

    private fun setDetailsContent(
        uiState: TrailerDetailsUiState,
        onAction: (TrailerDetailsUiAction) -> Unit = {},
        onBackClick: () -> Unit = {},
        onShareClick: () -> Unit = {},
        onYouTubeClick: () -> Unit = {},
        onPromotionalVideoClick: (String) -> Unit = {},
    ) {
        composeRule.setContent {
            CinematecaTheme {
                TrailerDetailsScreen(
                    uiState = uiState,
                    onAction = onAction,
                    onBackClick = onBackClick,
                    onShareClick = onShareClick,
                    onYouTubeClick = onYouTubeClick,
                    onPromotionalVideoClick = onPromotionalVideoClick,
                    testId = DETAILS_ID,
                )
            }
        }
    }

    private companion object {
        const val DETAILS_ID = "details_under_test"
    }
}
