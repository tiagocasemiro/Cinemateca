package com.cinemateca.features.trailers.home

import com.cinemateca.domain.trailers.model.Trailer

data class HomeUiState(
    val isLoading: Boolean = false,
    val trailers: List<Trailer> = emptyList(),
    val errorMessage: String? = null,
)
