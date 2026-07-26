package com.cinemateca.features.trailers.details

data class TrailerDetailsUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val details: TrailerDetailsUiModel? = null,
    val errorMessage: String? = null,
)

data class TrailerDetailsUiModel(
    val movieId: String,
    val trailerId: String,
    val title: String,
    val thumbnailUrl: String?,
    val topBadge: String,
    val views: String,
    val videoCount: String,
    val published: String,
    val tags: List<String>,
    val description: String,
    val promotionalVideos: List<PromotionalVideoUiModel>,
    val youtubeVideoId: String?,
    val isFavorite: Boolean,
    val isWatchlisted: Boolean,
)

data class PromotionalVideoUiModel(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val subtitle: String,
    val youtubeVideoId: String?,
)
