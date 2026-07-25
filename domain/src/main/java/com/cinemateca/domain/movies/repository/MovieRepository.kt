package com.cinemateca.domain.movies.repository

import com.cinemateca.domain.Result
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MovieVideoFilters

interface MovieRepository {
    interface Remote {
        suspend fun getByKinoCheckId(
            id: String,
            filters: MovieVideoFilters = MovieVideoFilters(),
        ): Result<Movie>

        suspend fun getByTmdbId(
            tmdbId: Int,
            filters: MovieVideoFilters = MovieVideoFilters(),
        ): Result<Movie>

        suspend fun getByImdbId(
            imdbId: String,
            filters: MovieVideoFilters = MovieVideoFilters(),
        ): Result<Movie>
    }
}
