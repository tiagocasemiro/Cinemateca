package com.cinemateca.domain.trailers.usecase

import com.cinemateca.domain.Result
import com.cinemateca.domain.trailers.model.TrailerFilters
import com.cinemateca.domain.trailers.model.TrailerPage
import com.cinemateca.domain.trailers.repository.TrailerRepository

class GetTrailersUseCase(
    private val repository: TrailerRepository.Remote,
) {
    suspend operator fun invoke(
        filters: TrailerFilters = TrailerFilters(),
    ): Result<TrailerPage> {
        return repository.getTrailers(filters)
    }
}
