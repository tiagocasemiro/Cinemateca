package com.cinemateca.features.trailers.home

import androidx.annotation.StringRes
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.UiText

data class HomeUiState(
    val isLoading: Boolean = false,
    val isOffline: Boolean = false,
    val sortOption: HomeSortOption = HomeSortOption.MostRecent,
    val filterOption: HomeFilterOption = HomeFilterOption.All,
    val searchQuery: String = "",
    val favoriteCount: Int = 0,
    val watchlistCount: Int = 0,
    val trailers: List<HomeTrailerItemUiModel> = emptyList(),
    val errorMessage: UiText? = null,
)

enum class HomeSortOption(
    @StringRes val labelResource: Int,
) {
    MostRecent(labelResource = R.string.sort_most_recent),
    MostPopular(labelResource = R.string.sort_most_popular),
    Alphabetical(labelResource = R.string.sort_alphabetical),
}

enum class HomeFilterOption(
    @StringRes val labelResource: Int,
) {
    All(labelResource = R.string.filter_all),
    NowPlaying(labelResource = R.string.filter_now_playing),
    Releases(labelResource = R.string.filter_releases),
    Upcoming(labelResource = R.string.filter_upcoming),
}

data class HomeTrailerItemUiModel(
    val id: String,
    val movieId: String,
    val resourceType: String,
    val title: String,
    val thumbnailUrl: String?,
    val genres: UiText,
    val published: UiText,
    val isFavorite: Boolean = false,
    val isWatchlisted: Boolean = false,
)
