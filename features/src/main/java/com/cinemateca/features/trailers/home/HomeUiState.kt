package com.cinemateca.features.trailers.home

data class HomeUiState(
    val isLoading: Boolean = false,
    val trailers: List<HomeTrailerItemUiModel> = emptyList(),
    val errorMessage: String? = null,
)

data class HomeTrailerItemUiModel(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val genres: String,
    val published: String,
)
