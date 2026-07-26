package com.cinemateca.features.trailers.home

sealed interface HomeUiAction {
    data object Refresh : HomeUiAction
    data object Retry : HomeUiAction
    data class SelectSortOption(
        val option: HomeSortOption,
    ) : HomeUiAction
}
