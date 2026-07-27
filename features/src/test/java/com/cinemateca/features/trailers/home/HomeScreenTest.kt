package com.cinemateca.features.trailers.home

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsSelected
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
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
        setHomeContent(
            uiState = HomeUiState(trailers = listOf(trailer())),
        )

        composeRule.onNodeWithTag(HOME_ID).assertIsDisplayed()
        composeRule.onNodeWithTag("$HOME_ID.header").assertIsDisplayed()
        composeRule.onNodeWithTag("$HOME_ID.header.search").assertIsDisplayed()
        composeRule.onNodeWithTag("$HOME_ID.filters").assertIsDisplayed()
        composeRule.onNodeWithTag("$HOME_ID.content").assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.content.movie.transformers")
            .assertIsDisplayed()
        composeRule.onNodeWithText("Transformers: O Início").assertIsDisplayed()
    }

    @Test
    fun `forwards search text changes from the movie input`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(trailers = listOf(trailer())),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.header.search")
            .performTextInput("Trans")

        assertEquals(
            listOf(HomeUiAction.SearchQueryChanged("Trans")),
            actions,
        )
    }

    @Test
    fun `renders and forwards the Figma clear search action`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(
                searchQuery = "Tr",
                trailers = listOf(trailer()),
            ),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.header.clear_search")
            .assertIsDisplayed()
            .performClick()

        assertEquals(
            listOf(HomeUiAction.SearchQueryChanged("")),
            actions,
        )
    }

    @Test
    fun `forwards retry action from error state`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(
                errorMessage = UiText.Dynamic("Falha ao carregar"),
            ),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.error.retry")
            .performClick()

        assertEquals(listOf(HomeUiAction.Retry), actions)
    }

    @Test
    fun `renders Figma skeletons while initial data is loading`() {
        setHomeContent(uiState = HomeUiState(isLoading = true))

        composeRule
            .onNodeWithTag("$HOME_ID.loading")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.loading.movie.0")
            .assertIsDisplayed()
    }

    @Test
    fun `renders Figma offline state and forwards retry action`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(isOffline = true),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.offline")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.offline.icon")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.offline.retry")
            .performClick()

        assertEquals(listOf(HomeUiAction.Retry), actions)
    }

    @Test
    fun `opens sort sheet and forwards the selected option`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(trailers = listOf(trailer())),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.filters.sort")
            .performClick()

        composeRule.onNodeWithTag("$HOME_ID.sort_sheet").assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.sort_sheet.option.mostpopular")
            .performClick()

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
        setHomeContent(
            uiState = HomeUiState(
                sortOption = HomeSortOption.Alphabetical,
                trailers = listOf(trailer()),
            ),
        )

        composeRule
            .onNodeWithTag("$HOME_ID.filters.sort")
            .assertIsDisplayed()
    }

    @Test
    fun `forwards filter selection and renders the selected chip`() {
        val actions = mutableListOf<HomeUiAction>()
        setHomeContent(
            uiState = HomeUiState(
                filterOption = HomeFilterOption.Upcoming,
                trailers = listOf(trailer()),
            ),
            onAction = actions::add,
        )

        composeRule
            .onNodeWithTag("$HOME_ID.filters.filter.upcoming")
            .assertIsSelected()
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.filters.filter.nowplaying")
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
        setHomeContent(
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

        composeRule
            .onNodeWithTag("$HOME_ID.header.favorite_count")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.header.watchlist_count")
            .assertIsDisplayed()
        composeRule
            .onNodeWithTag("$HOME_ID.content.movie.transformers.favorite")
            .assertIsSelected()
            .performClick()
        composeRule
            .onNodeWithTag("$HOME_ID.content.movie.transformers.watchlist")
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
        setHomeContent(
            uiState = HomeUiState(trailers = listOf(trailer())),
            onTrailerClick = { trailerId, movieId, resourceType ->
                clickedIds = Triple(trailerId, movieId, resourceType)
            },
        )

        composeRule
            .onNodeWithTag("$HOME_ID.content.movie.transformers")
            .performClick()

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

    private fun setHomeContent(
        uiState: HomeUiState,
        onAction: (HomeUiAction) -> Unit = {},
        onTrailerClick: (String, String, String) -> Unit = { _, _, _ -> },
    ) {
        composeRule.setContent {
            CinematecaTheme {
                HomeScreen(
                    uiState = uiState,
                    onAction = onAction,
                    onTrailerClick = onTrailerClick,
                    testId = HOME_ID,
                )
            }
        }
    }

    private companion object {
        const val HOME_ID = "home_under_test"
    }
}
