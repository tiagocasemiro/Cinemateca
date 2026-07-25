package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.Result
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.movies.repository.MovieRepository

class GetMovieByImdbIdUseCase(
    private val repository: MovieRepository.Remote,
) {
    suspend operator fun invoke(
        imdbId: String,
        filters: MovieVideoFilters = MovieVideoFilters(),
    ): Result<Movie> {
        require(imdbId.isNotBlank()) { "imdbId must not be blank" }
        return repository.getByImdbId(imdbId, filters)
    }
}
