package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveWatchlistMovieIdsUseCase(
    private val repository: WatchlistMovieRepository.Local,
) {
    operator fun invoke(): Flow<Set<String>> {
        return repository.observeMovieIds().distinctUntilChanged()
    }
}
