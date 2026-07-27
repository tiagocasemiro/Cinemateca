package com.cinemateca.features.trailers.home

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeDestination(
    onTrailerClick: (
        trailerId: String,
        movieId: String,
        resourceType: String,
    ) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
    testId: String? = null,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HomeScreen(
        uiState = uiState,
        onAction = viewModel::onAction,
        onTrailerClick = onTrailerClick,
        testId = testId,
    )
}
