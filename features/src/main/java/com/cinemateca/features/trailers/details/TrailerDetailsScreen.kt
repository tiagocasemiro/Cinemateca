package com.cinemateca.features.trailers.details

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.cinemateca.features.designsystem.CinematecaColors
import com.cinemateca.features.designsystem.CinematecaTheme
import com.cinemateca.features.trailers.details.components.TrailerDetailsBackground
import com.cinemateca.features.trailers.details.components.TrailerDetailsContent
import com.cinemateca.features.trailers.details.components.TrailerDetailsError
import com.cinemateca.features.trailers.details.components.TrailerDetailsLoading

@Composable
fun TrailerDetailsScreen(
    uiState: TrailerDetailsUiState,
    onAction: (TrailerDetailsUiAction) -> Unit,
    onBackClick: () -> Unit,
    onShareClick: () -> Unit,
    onYouTubeClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(CinematecaColors.Background),
    ) {
        TrailerDetailsBackground(
            imageUrl = uiState.details?.thumbnailUrl,
        )

        when {
            uiState.details != null -> TrailerDetailsContent(
                details = uiState.details,
                onBackClick = onBackClick,
                onShareClick = onShareClick,
                onFavoriteClick = {
                    onAction(TrailerDetailsUiAction.ToggleFavorite)
                },
                onWatchlistClick = {
                    onAction(TrailerDetailsUiAction.ToggleWatchlist)
                },
                onYouTubeClick = onYouTubeClick,
            )

            uiState.isLoading -> TrailerDetailsLoading(
                onBackClick = onBackClick,
            )

            uiState.errorMessage != null -> TrailerDetailsError(
                message = uiState.errorMessage,
                onBackClick = onBackClick,
                onRetry = {
                    onAction(TrailerDetailsUiAction.Retry)
                },
                modifier = Modifier.align(Alignment.Center),
            )

            else -> Text(
                text = "Trailer indisponível.",
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.align(Alignment.Center),
            )
        }
    }
}

@Preview(
    name = "Detalhes do trailer - Carregando",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun TrailerDetailsLoadingPreview() {
    CinematecaTheme {
        TrailerDetailsScreen(
            uiState = TrailerDetailsUiState(),
            onAction = {},
            onBackClick = {},
            onShareClick = {},
            onYouTubeClick = {},
        )
    }
}

@Preview(
    name = "Detalhes do trailer",
    showBackground = true,
    widthDp = 378,
    heightDp = 844,
)
@Composable
private fun TrailerDetailsScreenPreview() {
    CinematecaTheme {
        TrailerDetailsScreen(
            uiState = TrailerDetailsUiState(
                isLoading = false,
                details = TrailerDetailsUiModel(
                    movieId = "deadpool",
                    trailerId = "official",
                    title = "Deadpool & Wolverine",
                    thumbnailUrl = null,
                    topBadge = "Trailer",
                    views = "3.1M",
                    videoCount = "12 trailers",
                    published = "25 Jul 2024",
                    tags = listOf("#ação", "#comédia", "#inglês"),
                    description = "Wade Wilson recruta um relutante Wolverine " +
                        "para uma missão épica que envolve o multiverso.",
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
                    youtubeVideoId = "example",
                    isFavorite = false,
                    isWatchlisted = false,
                ),
            ),
            onAction = {},
            onBackClick = {},
            onShareClick = {},
            onYouTubeClick = {},
        )
    }
}
