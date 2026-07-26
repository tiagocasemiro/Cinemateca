package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.movies.repository.WatchlistMovieRepository

class ToggleWatchlistMovieUseCase(
    private val repository: WatchlistMovieRepository.Local,
) {
    suspend operator fun invoke(
        movieId: String,
        isCurrentlySelected: Boolean,
    ) {
        require(movieId.isNotBlank()) {
            "movieId must not be blank"
        }
        repository.setSelected(
            movieId = movieId,
            isSelected = !isCurrentlySelected,
        )
    }
}
