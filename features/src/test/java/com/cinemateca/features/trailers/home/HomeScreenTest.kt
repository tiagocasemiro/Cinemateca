package com.cinemateca.features.trailers.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.designsystem.UiText
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
        composeRule.onNodeWithText("Buscar filmes…").assertIsDisplayed()
        composeRule.onNodeWithText("Todos").assertIsDisplayed()
        composeRule.onNodeWithText("1 filme").assertIsDisplayed()
        composeRule.onNodeWithText("Transformers: O Início").assertIsDisplayed()
        composeRule.onNodeWithText("Favoritar").assertIsDisplayed()
        composeRule.onNodeWithText("Quero Assistir").assertIsDisplayed()
    }

    @Test
    fun `forwards search text changes from the movie input`() {
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
            .onNodeWithContentDescription("Buscar filmes")
            .performTextInput("Trans")

        assertEquals(
            listOf(HomeUiAction.SearchQueryChanged("Trans")),
            actions,
        )
    }

    @Test
    fun `renders and forwards the Figma clear search action`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        searchQuery = "Tr",
                        trailers = listOf(trailer()),
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule.onNodeWithText("Tr").assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("Limpar busca")
            .performClick()

        assertEquals(
            listOf(HomeUiAction.SearchQueryChanged("")),
            actions,
        )
    }

    @Test
    fun `forwards retry action from error state`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        errorMessage = UiText.Dynamic("Falha ao carregar"),
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

    @Test
    fun `forwards filter selection and renders the selected chip`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        filterOption = HomeFilterOption.Upcoming,
                        trailers = listOf(trailer()),
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("Filtro Em Breve selecionado")
            .assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("Filtrar por Em Cartaz")
            .performClick()

        assertEquals(
            listOf(
                HomeUiAction.SelectFilterOption(
                    HomeFilterOption.NowPlaying,
                ),
            ),
            actions,
        )
    }

    @Test
    fun `renders Figma selected movie actions and forwards toggles`() {
        val actions = mutableListOf<HomeUiAction>()
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        favoriteCount = 1,
                        watchlistCount = 1,
                        trailers = listOf(
                            trailer().copy(
                                isFavorite = true,
                                isWatchlisted = true,
                            ),
                        ),
                    ),
                    onAction = actions::add,
                )
            }
        }

        composeRule
            .onNodeWithContentDescription("1 favorito")
            .assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("1 quero assistir")
            .assertIsDisplayed()
        composeRule
            .onNodeWithContentDescription("Favoritar selecionado")
            .assertIsSelected()
            .performClick()
        composeRule
            .onNodeWithContentDescription("Quero Assistir selecionado")
            .assertIsSelected()
            .performClick()

        assertEquals(
            listOf(
                HomeUiAction.ToggleFavorite("transformers-movie"),
                HomeUiAction.ToggleWatchlist("transformers-movie"),
            ),
            actions,
        )
    }

    @Test
    fun `forwards trailer and movie ids when a card is clicked`() {
        var clickedIds: Triple<String, String, String>? = null
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = HomeUiState(
                        trailers = listOf(trailer()),
                    ),
                    onAction = {},
                    onTrailerClick = { trailerId, movieId, resourceType ->
                        clickedIds = Triple(
                            trailerId,
                            movieId,
                            resourceType,
                        )
                    },
                )
            }
        }

        composeRule.onNodeWithText("Transformers: O Início").performClick()

        assertEquals(
            Triple("transformers", "transformers-movie", "movie"),
            clickedIds,
        )
    }

    private fun trailer() = HomeTrailerItemUiModel(
        id = "transformers",
        movieId = "transformers-movie",
        resourceType = "movie",
        title = "Transformers: O Início",
        thumbnailUrl = null,
        genres = UiText.Dynamic("Ficção Científica / Ação"),
        published = UiText.Dynamic("Novembro 2024"),
    )
}
