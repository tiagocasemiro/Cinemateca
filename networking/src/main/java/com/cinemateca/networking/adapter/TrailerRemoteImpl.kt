package com.cinemateca.networking.adapter

import com.cinemateca.domain.Result
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.domain.trailers.repository.TrailerRepository
import com.cinemateca.networking.gateway.KinoCheckGateway
import com.cinemateca.repository.extractData
import com.cinemateca.repository.fetchData

internal class TrailerRemoteImpl(
    private val gateway: KinoCheckGateway,
) : TrailerRepository.Remote {
    override suspend fun getTrending(filters: TrailerFilters): Result<TrailerPage> {
        return fetchData {
            gateway.getTrending(
                genres = filters.genres.toQuery { value },
                categories = filters.categories.toQuery { value },
                language = filters.language?.code,
                page = filters.page,
                limit = filters.limit,
            ).extractData()
        }
    }

    override suspend fun getLatest(filters: TrailerFilters): Result<TrailerPage> {
        return fetchData {
            gateway.getLatest(
                genres = filters.genres.toQuery { value },
                categories = filters.categories.toQuery { value },
                language = filters.language?.code,
                page = filters.page,
                limit = filters.limit,
            ).extractData()
        }
    }

    override suspend fun getTrailers(filters: TrailerFilters): Result<TrailerPage> {
        return fetchData {
            gateway.getTrailers(
                genres = filters.genres.toQuery { value },
                categories = filters.categories.toQuery { value },
                language = filters.language?.code,
                page = filters.page,
                limit = filters.limit,
            ).extractData()
        }
    }
}

private fun <T> Set<T>.toQuery(value: T.() -> String): String? =
    takeIf { it.isNotEmpty() }?.joinToString(separator = ",") { it.value() }
