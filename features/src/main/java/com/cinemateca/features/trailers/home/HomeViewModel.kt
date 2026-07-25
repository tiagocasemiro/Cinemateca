package com.cinemateca.features.trailers.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cinemateca.domain.Failure
import com.cinemateca.domain.Loading
import com.cinemateca.domain.Success
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val getTrendingTrailersUseCase: GetTrendingTrailersUseCase,
) : ViewModel() {

    private val mutableUiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = mutableUiState.asStateFlow()

    private var loadTrendingJob: Job? = null

    init {
        loadTrending()
    }

    fun onAction(action: HomeUiAction) {
        when (action) {
            HomeUiAction.Refresh,
            HomeUiAction.Retry,
            -> loadTrending()
        }
    }

    private fun loadTrending() {
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
                    is Success -> mutableUiState.update {
                        it.copy(trailers = result.data.trailers)
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
