package com.cinemateca.features.trailers.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.trailers.home.components.HomeContent
import com.cinemateca.features.trailers.home.components.HomeErrorContent
import com.cinemateca.features.trailers.home.components.HomeFilters
import com.cinemateca.features.trailers.home.components.HomeHeader

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onAction: (HomeUiAction) -> Unit,
    modifier: Modifier = Modifier,
    onTrailerClick: (String) -> Unit = {},
    onFavoriteClick: (String) -> Unit = {},
    onWatchClick: (String) -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CinematecaColors.Background)
            .statusBarsPadding()
            .navigationBarsPadding(),
    ) {
        HomeHeader()
        HomeFilters(movieCount = uiState.trailers.size)

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(CinematecaColors.Background),
        ) {
            when {
                uiState.trailers.isNotEmpty() -> HomeContent(
                    trailers = uiState.trailers,
                    onTrailerClick = onTrailerClick,
                    onFavoriteClick = onFavoriteClick,
                    onWatchClick = onWatchClick,
                )

                uiState.isLoading -> CircularProgressIndicator(
                    color = CinematecaColors.Primary,
                    modifier = Modifier.align(Alignment.Center),
                )

                uiState.errorMessage != null -> HomeErrorContent(
                    message = uiState.errorMessage,
                    onRetry = {
                        onAction(HomeUiAction.Retry)
                    },
                    modifier = Modifier.align(Alignment.Center),
                )

                else -> Text(
                    text = "Nenhum filme em alta no momento.",
                    color = Color.White.copy(alpha = 0.5f),
                    modifier = Modifier.align(Alignment.Center),
                )
            }
        }
    }
}

private val previewTrailers = listOf(
    HomeTrailerItemUiModel(
        id = "transformers",
        title = "Transformers: O Início",
        thumbnailUrl = null,
        genres = "Ficção Científica / Ação",
        published = "Novembro 2024",
    ),
    HomeTrailerItemUiModel(
        id = "deadpool",
        title = "Deadpool & Wolverine",
        thumbnailUrl = null,
        genres = "Ação / Comédia",
        published = "25 Jul 2024",
    ),
    HomeTrailerItemUiModel(
        id = "wicked",
        title = "Wicked",
        thumbnailUrl = null,
        genres = "Musical / Drama",
        published = "22 Nov 2024",
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
            uiState = HomeUiState(trailers = previewTrailers),
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
                errorMessage = "Não foi possível carregar os filmes.",
            ),
            onAction = {},
        )
    }
}
