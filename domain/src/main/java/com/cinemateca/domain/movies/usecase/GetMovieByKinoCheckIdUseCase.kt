package com.cinemateca.domain.movies.usecase

import com.cinemateca.domain.Result
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MediaResourceType
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.movies.repository.MovieRepository

class GetMovieByKinoCheckIdUseCase(
    private val repository: MovieRepository.Remote,
) {
    suspend operator fun invoke(
        id: String,
        filters: MovieVideoFilters = MovieVideoFilters(),
        resourceType: MediaResourceType = MediaResourceType.Movie,
    ): Result<Movie> {
        require(id.isNotBlank()) { "id must not be blank" }
        return repository.getByKinoCheckId(id, filters, resourceType)
    }
}
