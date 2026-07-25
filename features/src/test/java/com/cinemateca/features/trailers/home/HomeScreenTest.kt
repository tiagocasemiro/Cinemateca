package com.cinemateca.features.trailers.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.cinemateca.features.designsystem.CinematecaTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class HomeScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `renders the Figma home structure and movie data`() {
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        trailers = listOf(trailer()),
                    ),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText("Cinemateca").assertIsDisplayed()
        composeRule.onNodeWithText("Buscar filmes...").assertIsDisplayed()
        composeRule.onNodeWithText("Todos").assertIsDisplayed()
        composeRule.onNodeWithText("1 filme").assertIsDisplayed()
        composeRule.onNodeWithText("Transformers: O Início").assertIsDisplayed()
        composeRule.onNodeWithText("Favoritar").assertIsDisplayed()
        composeRule.onNodeWithText("Quero Assistir").assertIsDisplayed()
    }

    @Test
    fun `forwards retry action from error state`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        errorMessage = "Falha ao carregar",
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNodeWithText("Tentar novamente").performClick()

        assertEquals(listOf(HomeUiAction.Retry), actions)
    }

    private fun trailer() = HomeTrailerItemUiModel(
        id = "transformers",
        title = "Transformers: O Início",
        thumbnailUrl = null,
        genres = "Ficção Científica / Ação",
        published = "Novembro 2024",
    )
}
