package com.cinemateca.features.trailers.home

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Loading
import com.cinemateca.domain.Success
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.model.Trailer
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import java.time.OffsetDateTime
import java.time.format.DateTimeFormatter
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

class HomeViewModel(
    private val getTrendingTrailersUseCase: GetTrendingTrailersUseCase,
    private val observeInternetConnectionUseCase: ObserveInternetConnectionUseCase,
    private val observeFavoriteMovieIdsUseCase: ObserveFavoriteMovieIdsUseCase,
    private val observeWatchlistMovieIdsUseCase: ObserveWatchlistMovieIdsUseCase,
    private val toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase,
    private val toggleWatchlistMovieUseCase: ToggleWatchlistMovieUseCase,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = mutableUiState.asStateFlow()

    private var loadTrendingJob: Job? = null
    private var hasRequestedInitialLoad = false
    private var loadedTrailers: List<Trailer> = emptyList()
    private var favoriteMovieIds: Set<String> = emptySet()
    private var watchlistMovieIds: Set<String> = emptySet()

    init {
        observeMovieSelections()
        observeInternetConnection()
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.Refresh,
            HomeUiAction.Retry,
            -> loadTrending()

            is HomeUiAction.SelectSortOption -> selectSortOption(action.option)
            is HomeUiAction.SelectFilterOption -> selectFilterOption(
                action.option,
            )
            is HomeUiAction.SearchQueryChanged -> search(action.query)
            is HomeUiAction.ToggleFavorite -> toggleFavorite(action.movieId)
            is HomeUiAction.ToggleWatchlist -> toggleWatchlist(action.movieId)
        }
    }

    private fun selectSortOption(option: HomeSortOption) {
        mutableUiState.update {
            it.copy(
                sortOption = option,
                trailers = loadedTrailers.toUiModels(
                    sortOption = option,
                    filterOption = it.filterOption,
                    searchQuery = it.searchQuery,
                    favoriteMovieIds = favoriteMovieIds,
                    watchlistMovieIds = watchlistMovieIds,
                ),
            )
        }
    }

    private fun selectFilterOption(option: HomeFilterOption) {
        mutableUiState.update {
            it.copy(
                filterOption = option,
                trailers = loadedTrailers.toUiModels(
                    sortOption = it.sortOption,
                    filterOption = option,
                    searchQuery = it.searchQuery,
                    favoriteMovieIds = favoriteMovieIds,
                    watchlistMovieIds = watchlistMovieIds,
                ),
            )
        }
    }

    private fun search(query: String) {
        mutableUiState.update {
            it.copy(
                searchQuery = query,
                trailers = loadedTrailers.toUiModels(
                    sortOption = it.sortOption,
                    filterOption = it.filterOption,
                    searchQuery = query,
                    favoriteMovieIds = favoriteMovieIds,
                    watchlistMovieIds = watchlistMovieIds,
                ),
            )
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
                mutableUiState.update {
                    it.copy(
                        favoriteCount = favoriteIds.size,
                        watchlistCount = watchlistIds.size,
                        trailers = loadedTrailers.toUiModels(
                            sortOption = it.sortOption,
                            filterOption = it.filterOption,
                            searchQuery = it.searchQuery,
                            favoriteMovieIds = favoriteIds,
                            watchlistMovieIds = watchlistIds,
                        ),
                    )
                }
            }
        }
    }

    private fun toggleFavorite(movieId: String) {
        viewModelScope.launch {
            runSelectionChange {
                toggleFavoriteMovieUseCase(
                    movieId = movieId,
                    isCurrentlySelected = movieId in favoriteMovieIds,
                )
            }
        }
    }

    private fun toggleWatchlist(movieId: String) {
        viewModelScope.launch {
            runSelectionChange {
                toggleWatchlistMovieUseCase(
                    movieId = movieId,
                    isCurrentlySelected = movieId in watchlistMovieIds,
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
                        loadTrendingJob?.cancel()
                    } else if (!hasRequestedInitialLoad || wasOffline) {
                        hasRequestedInitialLoad = true
                        loadTrending()
                    }
                }
        }
    }

    private fun loadTrending() {
        if (mutableUiState.value.isOffline) return
        if (loadTrendingJob?.isActive == true) return

        loadTrendingJob = viewModelScope.launch {
            mutableUiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null,
                )
            }

            try {
                when (val result = getTrendingTrailersUseCase()) {
                    is Success -> {
                        loadedTrailers = result.data.trailers
                        mutableUiState.update {
                            it.copy(
                                trailers = loadedTrailers.toUiModels(
                                    sortOption = it.sortOption,
                                    filterOption = it.filterOption,
                                    searchQuery = it.searchQuery,
                                    favoriteMovieIds = favoriteMovieIds,
                                    watchlistMovieIds = watchlistMovieIds,
                                ),
                            )
                        }
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

    private companion object {
        const val DEFAULT_ERROR_MESSAGE =
            "Não foi possível carregar os trailers em alta."
        const val SELECTION_ERROR_MESSAGE =
            "Não foi possível salvar sua seleção."
    }
}

@RequiresApi(Build.VERSION_CODES.O)
private fun List<Trailer>.toUiModels(
    sortOption: HomeSortOption,
    filterOption: HomeFilterOption,
    searchQuery: String,
    favoriteMovieIds: Set<String>,
    watchlistMovieIds: Set<String>,
): List<HomeTrailerItemUiModel> {
    val now = OffsetDateTime.now()
    val filteredTrailers = filter { trailer ->
        trailer.title.contains(
            other = searchQuery.trim(),
            ignoreCase = true,
        ) &&
            trailer.matchesFilter(
                filterOption = filterOption,
                now = now,
            )
    }

    val sortedTrailers = when (sortOption) {
        HomeSortOption.MostRecent -> filteredTrailers.sortedByDescending {
            it.published.orEmpty()
        }

        HomeSortOption.MostPopular -> filteredTrailers.sortedByDescending {
            it.views ?: 0L
        }

        HomeSortOption.Alphabetical -> filteredTrailers.sortedBy {
            it.title.lowercase(Locale.forLanguageTag("pt-BR"))
        }
    }

    return sortedTrailers.map { trailer ->
        val movieId = trailer.resource?.kinoCheckId ?: trailer.id
        trailer.toUiModel(
            movieId = movieId,
            resourceType = trailer.resource?.type.orEmpty(),
            isFavorite = movieId in favoriteMovieIds,
            isWatchlisted = movieId in watchlistMovieIds,
        )
    }
}

private fun Trailer.matchesFilter(
    filterOption: HomeFilterOption,
    now: OffsetDateTime,
): Boolean {
    if (filterOption == HomeFilterOption.All) return true

    val trailerPublishedAt = published
        ?.let { value ->
            runCatching {
                OffsetDateTime.parse(value)
            }.getOrNull()
        }
        ?: return false
    val movieReleaseAt = trailerPublishedAt.plusMonths(1)
    val movieLeavesTheatersAt = movieReleaseAt.plusWeeks(3)

    return when (filterOption) {
        HomeFilterOption.All -> true
        HomeFilterOption.NowPlaying ->
            !now.isBefore(movieReleaseAt) &&
                now.isBefore(movieLeavesTheatersAt)

        HomeFilterOption.Releases ->
            !now.isBefore(movieLeavesTheatersAt)

        HomeFilterOption.Upcoming ->
            now.isBefore(movieReleaseAt)
    }
}

private fun Trailer.toUiModel(
    movieId: String,
    resourceType: String,
    isFavorite: Boolean,
    isWatchlisted: Boolean,
) = HomeTrailerItemUiModel(
    id = id,
    movieId = movieId,
    resourceType = resourceType,
    title = title,
    thumbnailUrl = thumbnail ?: youtubeThumbnail,
    genres = genres
        .ifEmpty { categories }
        .joinToString(separator = " / ")
        .ifBlank { "Gênero não informado" },
    published = published.toDisplayDate(),
    isFavorite = isFavorite,
    isWatchlisted = isWatchlisted,
)

private fun String?.toDisplayDate(): String {
    if (isNullOrBlank()) return "Data não informada"

    return runCatching {
        OffsetDateTime.parse(this).format(
            DateTimeFormatter.ofPattern(
                "dd MMM uuuu",
                Locale.forLanguageTag("pt-BR"),
            ),
        )
    }.getOrDefault(this)
}
