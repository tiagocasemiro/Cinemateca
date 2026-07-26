package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveFavoriteMovieIdsUseCase(
    private val repository: FavoriteMovieRepository.Local,
) {
    operator fun invoke(): Flow<Set<String>> {
        return repository.observeMovieIds().distinctUntilChanged()
    }
}
