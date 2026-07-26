package com.cinemateca.features.trailers.home

sealed interface HomeUiAction {
    data object Refresh : HomeUiAction
    data object Retry : HomeUiAction
    data class SelectSortOption(
        val option: HomeSortOption,
    ) : HomeUiAction
    data class SelectFilterOption(
        val option: HomeFilterOption,
    ) : HomeUiAction
    data class SearchQueryChanged(
        val query: String,
    ) : HomeUiAction
    data class ToggleFavorite(
        val movieId: String,
    ) : HomeUiAction
    data class ToggleWatchlist(
        val movieId: String,
    ) : HomeUiAction
}
