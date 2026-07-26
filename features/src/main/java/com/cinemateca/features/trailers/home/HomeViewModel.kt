package com.cinemateca.features.trailers.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Loading
import com.cinemateca.domain.Success
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
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
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingTrailersUseCase: GetTrendingTrailersUseCase,
    private val observeInternetConnectionUseCase: ObserveInternetConnectionUseCase,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = mutableUiState.asStateFlow()

    private var loadTrendingJob: Job? = null
    private var hasRequestedInitialLoad = false
    private var loadedTrailers: List<Trailer> = emptyList()

    init {
        observeInternetConnection()
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.Refresh,
            HomeUiAction.Retry,
            -> loadTrending()

            is HomeUiAction.SelectSortOption -> selectSortOption(action.option)
        }
    }

    private fun selectSortOption(option: HomeSortOption) {
        mutableUiState.update {
            it.copy(
                sortOption = option,
                trailers = loadedTrailers.toUiModels(option),
            )
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
                                    it.sortOption,
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
    }
}

private fun List<Trailer>.toUiModels(
    sortOption: HomeSortOption,
): List<HomeTrailerItemUiModel> {
    val sortedTrailers = when (sortOption) {
        HomeSortOption.MostRecent -> sortedByDescending {
            it.published.orEmpty()
        }

        HomeSortOption.MostPopular -> sortedByDescending {
            it.views ?: 0L
        }

        HomeSortOption.Alphabetical -> sortedBy {
            it.title.lowercase(Locale.forLanguageTag("pt-BR"))
        }
    }

    return sortedTrailers.map(Trailer::toUiModel)
}

private fun Trailer.toUiModel() = HomeTrailerItemUiModel(
    id = id,
    title = title,
    thumbnailUrl = thumbnail ?: youtubeThumbnail,
    genres = genres
        .ifEmpty { categories }
        .joinToString(separator = " / ")
        .ifBlank { "Gênero não informado" },
    published = published.toDisplayDate(),
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
