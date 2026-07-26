package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.movies.repository.FavoriteMovieRepository

class ToggleFavoriteMovieUseCase(
    private val repository: FavoriteMovieRepository.Local,
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
