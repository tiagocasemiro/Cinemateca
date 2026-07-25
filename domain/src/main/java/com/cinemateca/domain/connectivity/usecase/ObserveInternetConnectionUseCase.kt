package com.cinemateca.domain.connectivity.usecase

import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged

class ObserveInternetConnectionUseCase(
    private val repository: InternetConnectionRepository.Local,
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.observeAvailability().distinctUntilChanged()
    }
}
