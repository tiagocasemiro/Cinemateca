package com.cinemateca.networking.adapter

import com.cinemateca.domain.Result
import com.cinemateca.domain.movies.model.Movie
import com.cinemateca.domain.movies.model.MovieVideoFilters
import com.cinemateca.domain.movies.repository.MovieRepository
import com.cinemateca.networking.gateway.KinoCheckGateway
import com.cinemateca.repository.extractData
import com.cinemateca.repository.fetchData

internal class MovieRemoteImpl(
    private val gateway: KinoCheckGateway,
) : MovieRepository.Remote {
    override suspend fun getByKinoCheckId(
        id: String,
        filters: MovieVideoFilters,
    ): Result<Movie> {
        require(id.isNotBlank()) { "id must not be blank" }
        return getMovie(id = id, filters = filters)
    }

    override suspend fun getByTmdbId(
        tmdbId: Int,
        filters: MovieVideoFilters,
    ): Result<Movie> {
        require(tmdbId > 0) { "tmdbId must be positive" }
        return getMovie(tmdbId = tmdbId, filters = filters)
    }

    override suspend fun getByImdbId(
        imdbId: String,
        filters: MovieVideoFilters,
    ): Result<Movie> {
        require(imdbId.isNotBlank()) { "imdbId must not be blank" }
        return getMovie(imdbId = imdbId, filters = filters)
    }

    private suspend fun getMovie(
        id: String? = null,
        tmdbId: Int? = null,
        imdbId: String? = null,
        filters: MovieVideoFilters,
    ): Result<Movie> {
        return fetchData {
            gateway.getMovie(
                id = id,
                tmdbId = tmdbId,
                imdbId = imdbId,
                categories = filters.categories
                    .takeIf { it.isNotEmpty() }
                    ?.joinToString(separator = ",") { it.value },
                language = filters.language?.code,
            ).extractData()
        }
    }
}
