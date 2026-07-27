package com.cinemateca.features.trailers.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.res.stringResource
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.designsystem.UiText
import com.cinemateca.features.designsystem.asString
import com.cinemateca.features.designsystem.childTestId
import com.cinemateca.features.designsystem.components.OfflineContent
import com.cinemateca.features.designsystem.testId
import com.cinemateca.features.trailers.home.components.HomeContent
import com.cinemateca.features.trailers.home.components.HomeErrorContent
import com.cinemateca.features.trailers.home.components.HomeFilters
import com.cinemateca.features.trailers.home.components.HomeHeader
import com.cinemateca.features.trailers.home.components.HomeLoadingContent
import com.cinemateca.features.trailers.home.components.HomeSortBottomSheet

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier,
    onTrailerClick: (
        trailerId: String,
        movieId: String,
        resourceType: String,
    ) -> Unit = { _, _, _ -> },
    testId: String? = null,
) {
    var isSortSheetVisible by rememberSaveable {
        mutableStateOf(false)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinematecaColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .testId(testId),
    ) {
        HomeHeader(
            searchQuery = uiState.searchQuery,
            favoriteCount = uiState.favoriteCount,
            watchlistCount = uiState.watchlistCount,
            onSearchQueryChange = { query ->
                onAction(HomeUiAction.SearchQueryChanged(query))
            },
            testId = testId.childTestId("header"),
        )
        HomeFilters(
            movieCount = uiState.trailers.size.takeUnless {
                uiState.isOffline ||
                    (uiState.isLoading && uiState.trailers.isEmpty())
            },
            sortOptionLabel = stringResource(uiState.sortOption.labelResource),
            selectedFilter = uiState.filterOption,
            onFilterClick = { option ->
                onAction(HomeUiAction.SelectFilterOption(option))
            },
            onSortClick = {
                isSortSheetVisible = true
            },
            testId = testId.childTestId("filters"),
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .background(CinematecaColors.Background),
        ) {
            when {
                uiState.isOffline -> OfflineContent(
                    onRetry = {
                        onAction(HomeUiAction.Retry)
                    },
                    testId = testId.childTestId("offline"),
                )

                uiState.trailers.isNotEmpty() -> HomeContent(
                    trailers = uiState.trailers,
                    onTrailerClick = onTrailerClick,
                    onFavoriteClick = { movieId ->
                        onAction(HomeUiAction.ToggleFavorite(movieId))
                    },
                    onWatchClick = { movieId ->
                        onAction(HomeUiAction.ToggleWatchlist(movieId))
                    },
                    testId = testId.childTestId("content"),
                )

                uiState.isLoading -> HomeLoadingContent(
                    testId = testId.childTestId("loading"),
                )

                uiState.errorMessage != null -> HomeErrorContent(
                    message = uiState.errorMessage.asString(),
                    onRetry = {
                        onAction(HomeUiAction.Retry)
                    },
                    modifier = Modifier.align(Alignment.Center),
                    testId = testId.childTestId("error"),
                )

                else -> Text(
                    text = stringResource(R.string.home_empty_message),
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testId(testId.childTestId("empty")),
                )
            }
        }
    }

    if (isSortSheetVisible) {
        HomeSortBottomSheet(
            selectedOption = uiState.sortOption,
            onOptionSelected = { option ->
                isSortSheetVisible = false
                onAction(HomeUiAction.SelectSortOption(option))
            },
            onDismiss = {
                isSortSheetVisible = false
            },
            testId = testId.childTestId("sort_sheet"),
        )
    }
}

private val previewTrailers = listOf(
    HomeTrailerItemUiModel(
        id = "transformers",
        movieId = "transformers-movie",
        resourceType = "movie",
        title = "Transformers: O Início",
        thumbnailUrl = null,
        genres = UiText.Dynamic("Ficção Científica / Ação"),
        published = UiText.Dynamic("Novembro 2024"),
        isFavorite = true,
        isWatchlisted = true,
    ),
    HomeTrailerItemUiModel(
        id = "deadpool",
        movieId = "deadpool-movie",
        resourceType = "movie",
        title = "Deadpool & Wolverine",
        thumbnailUrl = null,
        genres = UiText.Dynamic("Ação / Comédia"),
        published = UiText.Dynamic("25 Jul 2024"),
    ),
    HomeTrailerItemUiModel(
        id = "wicked",
        movieId = "wicked-movie",
        resourceType = "movie",
        title = "Wicked",
        thumbnailUrl = null,
        genres = UiText.Dynamic("Musical / Drama"),
        published = UiText.Dynamic("22 Nov 2024"),
    ),
)

@Preview(
    name = "Home - Conteúdo",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun HomeContentPreview() {
    CinematecaTheme {
        HomeScreen(
            uiState = HomeUiState(
                favoriteCount = 1,
                watchlistCount = 1,
                trailers = previewTrailers,
            ),
            onAction = {},
        )
    }
}

@Preview(
    name = "Home - Carregando",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun HomeLoadingPreview() {
    CinematecaTheme {
        HomeScreen(
            uiState = HomeUiState(isLoading = true),
            onAction = {},
        )
    }
}

@Preview(
    name = "Home - Erro",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun HomeErrorPreview() {
    CinematecaTheme {
        HomeScreen(
            uiState = HomeUiState(
                errorMessage = UiText.Resource(R.string.home_default_error),
            ),
            onAction = {},
        )
    }
}

@Preview(
    name = "Home - Sem conexão",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun HomeOfflinePreview() {
    CinematecaTheme {
        HomeScreen(
            uiState = HomeUiState(isOffline = true),
            onAction = {},
        )
    }
}
