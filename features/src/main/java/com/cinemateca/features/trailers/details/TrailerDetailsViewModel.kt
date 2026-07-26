package com.cinemateca.features.trailers.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Loading
import com.cinemateca.domain.Success
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.features.R
import com.cinemateca.features.designsystem.UiText
import com.cinemateca.navigation.TrailerDetailsRoute
import java.util.Locale
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class TrailerDetailsViewModel(
    savedStateHandle: SavedStateHandle,
    private val getMovieByKinoCheckIdUseCase: GetMovieByKinoCheckIdUseCase,
    private val observeInternetConnectionUseCase: ObserveInternetConnectionUseCase,
    private val observeFavoriteMovieIdsUseCase: ObserveFavoriteMovieIdsUseCase,
    private val observeWatchlistMovieIdsUseCase: ObserveWatchlistMovieIdsUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val toggleWatchlistMovieUseCase: ToggleWatchlistMovieUseCase,
) : ViewModel() {
    private val route = savedStateHandle.toRoute<TrailerDetailsRoute>()
    private val mutableUiState = MutableStateFlow(TrailerDetailsUiState())
    val uiState: StateFlow<TrailerDetailsUiState> = mutableUiState.asStateFlow()

    private var favoriteMovieIds: Set<String> = emptySet()
    private var watchlistMovieIds: Set<String> = emptySet()
    private var loadDetailsJob: Job? = null
    private var hasRequestedInitialLoad = false

    init {
        observeMovieSelections()
        observeInternetConnection()
    }

    fun onAction(action: TrailerDetailsUiAction) {
        when (action) {
            TrailerDetailsUiAction.Retry -> loadDetails()
            TrailerDetailsUiAction.ToggleFavorite -> toggleFavorite()
            TrailerDetailsUiAction.ToggleWatchlist -> toggleWatchlist()
        }
    }

    private fun observeMovieSelections() {
        viewModelScope.launch {
            combine(
                observeFavoriteMovieIdsUseCase(),
                observeWatchlistMovieIdsUseCase(),
            ) { favoriteIds, watchlistIds ->
                favoriteIds to watchlistIds
            }.catch { throwable ->
                if (throwable is CancellationException) throw throwable
                emit(emptySet<String>() to emptySet())
            }.collect { (favoriteIds, watchlistIds) ->
                favoriteMovieIds = favoriteIds
                watchlistMovieIds = watchlistIds
                mutableUiState.update { state ->
                    state.copy(
                        details = state.details?.copy(
                            isFavorite = route.movieId in favoriteIds,
                            isWatchlisted = route.movieId in watchlistIds,
                        ),
                    )
                }
            }
        }
    }

    private fun loadDetails() {
        if (mutableUiState.value.isOffline) return
        if (loadDetailsJob?.isActive == true) return

        loadDetailsJob = viewModelScope.launch {
            mutableUiState.update {
                it.copy(isLoading = true, errorMessage = null)
            }

            try {
                when (
                    val result = getMovieByKinoCheckIdUseCase(
                        id = route.movieId,
                        resourceType = MediaResourceType.fromApiValue(
                            route.resourceType,
                        ),
                    )
                ) {
                    is Success -> mutableUiState.update {
                        it.copy(
                            details = result.data.toUiModel(
                                trailerId = route.trailerId,
                                isFavorite = route.movieId in favoriteMovieIds,
                                isWatchlisted = route.movieId in watchlistMovieIds,
                            ),
                        )
                    }

                    is Failure -> mutableUiState.update {
                        it.copy(
                            errorMessage = UiText.Resource(
                                R.string.details_default_error,
                            ),
                        )
                    }

                    is Loading<*> -> Unit
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Throwable) {
                mutableUiState.update {
                    it.copy(
                        errorMessage = UiText.Resource(
                            R.string.details_default_error,
                        ),
                    )
                }
            } finally {
                mutableUiState.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }

    private fun observeInternetConnection() {
        viewModelScope.launch {
            observeInternetConnectionUseCase()
                .catch { throwable ->
                    if (throwable is CancellationException) throw throwable
                    emit(false)
                }
                .collect { isAvailable ->
                    val wasOffline = mutableUiState.value.isOffline

                    mutableUiState.update {
                        it.copy(
                            isOffline = !isAvailable,
                            isLoading = if (isAvailable) {
                                it.isLoading
                            } else {
                                false
                            },
                            errorMessage = if (isAvailable) {
                                it.errorMessage
                            } else {
                                null
                            },
                        )
                    }

                    if (!isAvailable) {
                        loadDetailsJob?.cancel()
                    } else if (!hasRequestedInitialLoad || wasOffline) {
                        hasRequestedInitialLoad = true
                        loadDetails()
                    }
                }
        }
    }

    private fun toggleFavorite() {
        val details = mutableUiState.value.details ?: return
        viewModelScope.launch {
            runSelectionChange {
                toggleFavoriteMovieUseCase(
                    movieId = details.movieId,
                    isCurrentlySelected = details.isFavorite,
                )
            }
        }
    }

    private fun toggleWatchlist() {
        val details = mutableUiState.value.details ?: return
        viewModelScope.launch {
            runSelectionChange {
                toggleWatchlistMovieUseCase(
                    movieId = details.movieId,
                    isCurrentlySelected = details.isWatchlisted,
                )
            }
        }
    }

    private suspend fun runSelectionChange(
        change: suspend () -> Unit,
    ) {
        try {
            change()
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Throwable) {
            mutableUiState.update {
                it.copy(
                    errorMessage = UiText.Resource(R.string.selection_error),
                )
            }
        }
    }
}

private fun Movie.toUiModel(
    trailerId: String,
    isFavorite: Boolean,
    isWatchlisted: Boolean,
): TrailerDetailsUiModel {
    val selectedTrailer = videos.firstOrNull { it.id == trailerId }
        ?: trailer?.takeIf { it.id == trailerId }
        ?: trailer
        ?: videos.firstOrNull()
        ?: error("Movie has no trailers")
    val allVideos = videos.ifEmpty { listOf(selectedTrailer) }
    val displayTitle = title.ifBlank { selectedTrailer.title }

    return TrailerDetailsUiModel(
        movieId = id,
        trailerId = selectedTrailer.id,
        title = displayTitle,
        thumbnailUrl = selectedTrailer.thumbnail
            ?: selectedTrailer.youtubeThumbnail,
        topBadge = selectedTrailer.categories.firstOrNull()
            ?.let(UiText::Dynamic)
            ?: UiText.Resource(R.string.details_default_badge),
        views = selectedTrailer.views.toCompactViews(),
        videoCount = UiText.plural(
            R.plurals.trailer_count,
            allVideos.size,
            allVideos.size,
        ),
        published = selectedTrailer.published.toDisplayDate(),
        tags = selectedTrailer.genres
            .ifEmpty { selectedTrailer.categories }
            .take(4)
            .map { tag ->
                "#${tag.lowercase(Locale.forLanguageTag("pt-BR")).replace(' ', '_')}"
            },
        description = UiText.resource(
            R.string.details_description,
            displayTitle,
        ),
        promotionalVideos = allVideos
            .sortedByDescending { it.id == selectedTrailer.id }
            .take(5)
            .map(Trailer::toPromotionalVideo),
        youtubeVideoId = selectedTrailer.youtubeVideoId,
        isFavorite = isFavorite,
        isWatchlisted = isWatchlisted,
    )
}

private fun Trailer.toPromotionalVideo() = PromotionalVideoUiModel(
    id = id,
    title = title,
    thumbnailUrl = thumbnail ?: youtubeThumbnail,
    subtitle = categories.firstOrNull()
        ?.let(UiText::Dynamic)
        ?: published.toDisplayDate(),
    youtubeVideoId = youtubeVideoId,
)

private fun Long?.toCompactViews(): UiText {
    val value = this ?: return UiText.Resource(R.string.not_available_symbol)
    return when {
        value >= 1_000_000 -> UiText.resource(
            R.string.compact_millions,
            formatCompact(value / 1_000_000.0),
        )
        value >= 1_000 -> UiText.resource(
            R.string.compact_thousands,
            formatCompact(value / 1_000.0),
        )
        else -> UiText.Dynamic(value.toString())
    }
}

private fun formatCompact(
    value: Double,
): String {
    val formatted = String.format(Locale.US, "%.1f", value)
        .removeSuffix(".0")
    return formatted
}

private fun String?.toDisplayDate(): UiText {
    if (isNullOrBlank()) {
        return UiText.Resource(R.string.not_available_symbol)
    }
    val match = DATE_PATTERN.find(this) ?: return UiText.Dynamic(this)
    val (year, month, day) = match.destructured
    val monthIndex = month.toIntOrNull()?.minus(1)
        ?: return UiText.Dynamic(this)
    val monthResource = MONTH_RESOURCES.getOrNull(monthIndex)
        ?: return UiText.Dynamic(this)
    return UiText.resource(
        R.string.display_date,
        day.toInt(),
        UiText.Resource(monthResource),
        year,
    )
}

private val DATE_PATTERN = Regex("""^(\d{4})-(\d{2})-(\d{2})""")
private val MONTH_RESOURCES = listOf(
    R.string.month_january_short,
    R.string.month_february_short,
    R.string.month_march_short,
    R.string.month_april_short,
    R.string.month_may_short,
    R.string.month_june_short,
    R.string.month_july_short,
    R.string.month_august_short,
    R.string.month_september_short,
    R.string.month_october_short,
    R.string.month_november_short,
    R.string.month_december_short,
)
