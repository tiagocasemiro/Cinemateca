package com.cinemateca.features.trailers.details

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.toRoute
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Loading
import com.cinemateca.domain.Success
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.navigation.TrailerDetailsRoute
import java.util.Locale
import kotlinx.coroutines.CancellationException
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

    init {
        observeMovieSelections()
        loadDetails()
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
        viewModelScope.launch {
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
                            errorMessage = result.error
                                ?.formattedMessage
                                .orEmpty()
                                .ifBlank { DEFAULT_ERROR_MESSAGE },
                        )
                    }

                    is Loading<*> -> Unit
                }
            } catch (cancellation: CancellationException) {
                throw cancellation
            } catch (_: Throwable) {
                mutableUiState.update {
                    it.copy(errorMessage = DEFAULT_ERROR_MESSAGE)
                }
            } finally {
                mutableUiState.update {
                    it.copy(isLoading = false)
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
                it.copy(errorMessage = SELECTION_ERROR_MESSAGE)
            }
        }
    }

    private companion object {
        const val DEFAULT_ERROR_MESSAGE =
            "Não foi possível carregar os detalhes deste trailer."
        const val SELECTION_ERROR_MESSAGE =
            "Não foi possível salvar sua seleção."
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
        topBadge = selectedTrailer.categories.firstOrNull() ?: "Trailer",
        views = selectedTrailer.views.toCompactViews(),
        videoCount = "${allVideos.size} ${if (allVideos.size == 1) "trailer" else "trailers"}",
        published = selectedTrailer.published.toDisplayDate(),
        tags = selectedTrailer.genres
            .ifEmpty { selectedTrailer.categories }
            .take(4)
            .map { tag ->
                "#${tag.lowercase(Locale.forLanguageTag("pt-BR")).replace(' ', '_')}"
            },
        description = "Assista ao trailer de $displayTitle e confira " +
            "os principais destaques desta produção.",
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
    subtitle = categories.firstOrNull() ?: published.toDisplayDate(),
)

private fun Long?.toCompactViews(): String {
    val value = this ?: return "—"
    return when {
        value >= 1_000_000 -> formatCompact(value / 1_000_000.0, "M")
        value >= 1_000 -> formatCompact(value / 1_000.0, "K")
        else -> value.toString()
    }
}

private fun formatCompact(
    value: Double,
    suffix: String,
): String {
    val formatted = String.format(Locale.US, "%.1f", value)
        .removeSuffix(".0")
    return "$formatted$suffix"
}

private fun String?.toDisplayDate(): String {
    if (isNullOrBlank()) return "—"
    val match = DATE_PATTERN.find(this) ?: return this
    val (year, month, day) = match.destructured
    val monthIndex = month.toIntOrNull()?.minus(1) ?: return this
    val monthName = MONTHS.getOrNull(monthIndex) ?: return this
    return "${day.toInt()} $monthName $year"
}

private val DATE_PATTERN = Regex("""^(\d{4})-(\d{2})-(\d{2})""")
private val MONTHS = listOf(
    "Jan",
    "Fev",
    "Mar",
    "Abr",
    "Mai",
    "Jun",
    "Jul",
    "Ago",
    "Set",
    "Out",
    "Nov",
    "Dez",
)
