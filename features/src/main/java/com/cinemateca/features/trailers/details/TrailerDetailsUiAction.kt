package com.cinemateca.features.trailers.details

sealed interface TrailerDetailsUiAction {
    data object Retry : TrailerDetailsUiAction
    data object ToggleFavorite : TrailerDetailsUiAction
    data object ToggleWatchlist : TrailerDetailsUiAction
}
