package com.cinemateca.features.trailers.details

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.cinemateca.features.designsystem.CinematecaTheme
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
    fun `renders the complete trailer details layout`() {
        composeRule.setContent {
            CinematecaTheme {
                TrailerDetailsScreen(
                    uiState = TrailerDetailsUiState(
                        isLoading = false,
                        details = details(),
                    ),
                    onAction = {},
                    onBackClick = {},
                    onShareClick = {},
                    onYouTubeClick = {},
                )
            }
        }

        composeRule
            .onNodeWithText("Deadpool & Wolverine")
            .assertIsDisplayed()
        composeRule.onNodeWithText("3.1M").assertIsDisplayed()
        composeRule.onNodeWithText("12 trailers").assertIsDisplayed()
        composeRule
            .onNodeWithText("#ação")
            .assertExists()
        composeRule
            .onNodeWithText("DESCRIÇÃO DO TRAILER")
            .performScrollTo()
            .assertIsDisplayed()
        composeRule
            .onNodeWithText("Assistir no YouTube")
            .performScrollTo()
            .assertIsDisplayed()
    }

    @Test
    fun `forwards header and trailer action clicks`() {
        val callbacks = mutableListOf<String>()
        composeRule.setContent {
            CinematecaTheme {
                TrailerDetailsScreen(
                    uiState = TrailerDetailsUiState(
                        isLoading = false,
                        details = details().copy(
                            isFavorite = true,
                            isWatchlisted = true,
                        ),
                    ),
                    onAction = { action ->
                        callbacks += action.toString()
                    },
                    onBackClick = { callbacks += "back" },
                    onShareClick = { callbacks += "share" },
                    onYouTubeClick = { callbacks += "youtube" },
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Voltar")
            .performClick()
        composeRule
            .onNodeWithContentDescription("Compartilhar trailer")
            .performClick()
        composeRule.onNodeWithText("Favoritado")
            .assertIsSelected()
            .performClick()
        composeRule.onNodeWithText("Na Lista")
            .assertIsSelected()
            .performClick()
        composeRule.onNodeWithText("Assistir no YouTube")
            .performScrollTo()
            .performClick()

        assertEquals(
            listOf(
                "back",
                "share",
                TrailerDetailsUiAction.ToggleFavorite.toString(),
                TrailerDetailsUiAction.ToggleWatchlist.toString(),
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
        topBadge = "Trailer",
        views = "3.1M",
        videoCount = "12 trailers",
        published = "25 Jul 2024",
        tags = listOf("#ação", "#comédia", "#inglês"),
        description = "Descrição de exemplo.",
        promotionalVideos = listOf(
            PromotionalVideoUiModel(
                id = "official",
                title = "Trailer oficial — Deadpool & Wolverine",
                thumbnailUrl = null,
                subtitle = "Trailer",
            ),
            PromotionalVideoUiModel(
                id = "teaser",
                title = "Teaser — Deadpool & Wolverine",
                thumbnailUrl = null,
                subtitle = "Teaser",
            ),
        ),
        youtubeVideoId = "youtube-id",
        isFavorite = false,
        isWatchlisted = false,
    )
}
