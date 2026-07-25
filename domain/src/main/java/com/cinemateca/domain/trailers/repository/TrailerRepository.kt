package com.cinemateca.domain.trailers.repository

import com.cinemateca.domain.Result
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerPage

interface TrailerRepository {
    interface Remote {
        suspend fun getTrending(filters: TrailerFilters = TrailerFilters()): Result<TrailerPage>

        suspend fun getLatest(filters: TrailerFilters = TrailerFilters()): Result<TrailerPage>

        suspend fun getTrailers(filters: TrailerFilters = TrailerFilters()): Result<TrailerPage>
    }
}
