package com.cinemateca.features.trailers.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
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

    @Test
    fun `renders Figma skeletons while initial data is loading`() {
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(isLoading = true),
                    onAction = {},
                )
            }
        }

        composeRule.onNodeWithText("—").assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("Carregando filmes")
            .assertIsDisplayed()
    }

    @Test
    fun `renders Figma offline state and forwards retry action`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(isOffline = true),
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNodeWithText("Sem conexão").assertIsDisplayed()
        composeRule
            .onNodeWithText("Verifique sua conexão e tente novamente.")
            .assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("Sem conexão com a internet")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Tentar novamente").performClick()

        assertEquals(listOf(HomeUiAction.Retry), actions)
    }

    @Test
    fun `opens sort sheet and forwards the selected option`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        trailers = listOf(trailer()),
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule
            .onNodeWithContentDescription(
                "Ordenar filmes: Mais Recentes",
            )
            .performClick()

        composeRule.onNodeWithText("Ordenar por").assertIsDisplayed()
        composeRule.onNodeWithText("Mais Populares").performClick()

        assertEquals(
            listOf(
                HomeUiAction.SelectSortOption(
                    HomeSortOption.MostPopular,
                ),
            ),
            actions,
        )
    }

    @Test
    fun `renders the selected sort option beside the sort icon`() {
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        sortOption = HomeSortOption.Alphabetical,
                        trailers = listOf(trailer()),
                    ),
                    onAction = {},
                )
            }
        }

        composeRule
            .onNodeWithContentDescription(
                "Ordenar filmes: Ordem Alfabética",
            )
            .assertIsDisplayed()
    }

    private fun trailer() = HomeTrailerItemUiModel(
        id = "transformers",
        title = "Transformers: O Início",
        thumbnailUrl = null,
        genres = "Ficção Científica / Ação",
        published = "Novembro 2024",
    )
}
