package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.Result
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.movies.repository.MovieRepository

class GetMovieByTmdbIdUseCase(
    private val repository: MovieRepository.Remote,
) {
    suspend operator fun invoke(
        tmdbId: Int,
        filters: MovieVideoFilters = MovieVideoFilters(),
    ): Result<Movie> {
        require(tmdbId > 0) { "tmdbId must be positive" }
        return repository.getByTmdbId(tmdbId, filters)
    }
}
