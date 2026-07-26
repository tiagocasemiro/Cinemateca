package com.cinemateca.features.trailers.details

import com.cinemateca.features.designsystem.UiText

data class TrailerDetailsUiState(
    val isLoading: Boolean = true,
    val isOffline: Boolean = false,
    val details: TrailerDetailsUiModel? = null,
    val errorMessage: UiText? = null,
)

data class TrailerDetailsUiModel(
    val movieId: String,
    val trailerId: String,
    val title: String,
    val thumbnailUrl: String?,
    val topBadge: UiText,
    val views: UiText,
    val videoCount: UiText,
    val published: UiText,
    val tags: List<String>,
    val description: UiText,
    val promotionalVideos: List<PromotionalVideoUiModel>,
    val youtubeVideoId: String?,
    val isFavorite: Boolean,
    val isWatchlisted: Boolean,
)

data class PromotionalVideoUiModel(
    val id: String,
    val title: String,
    val thumbnailUrl: String?,
    val subtitle: UiText,
    val youtubeVideoId: String?,
)
