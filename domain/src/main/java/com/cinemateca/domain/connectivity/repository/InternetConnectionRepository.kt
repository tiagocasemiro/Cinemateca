package com.cinemateca.domain.connectivity.repository

import kotlinx.coroutines.flow.Flow

interface InternetConnectionRepository {
    interface Local {
        fun observeAvailability(): Flow<Boolean>
    }
}
