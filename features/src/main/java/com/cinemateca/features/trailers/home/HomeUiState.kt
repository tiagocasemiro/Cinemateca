package com.cinemateca.features.trailers.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val sortOption: HomeSortOption = HomeSortOption.MostRecent,
    val trailers: List<HomeTrailerItemUiModel> = emptyList(),
    val errorMessage: String? = null,
)

enum class HomeSortOption(
    val label: String,
) {
    MostRecent(label = "Mais Recentes"),
    MostPopular(label = "Mais Populares"),
    Alphabetical(label = "Ordem Alfabética"),
}

data class HomeTrailerItemUiModel(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val genres: String,
    val published: String,
)
