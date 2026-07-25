package com.cinemateca.domain.connectivity.usecase

import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test

class ObserveInternetConnectionUseCaseTest {
    @Test
    fun `exposes availability changes without consecutive duplicates`() =
        runTest {
            val useCase = ObserveInternetConnectionUseCase(
                repository = FakeInternetConnectionRepository(
                    availability = listOf(true, true, false, false, true).asFlow(),
                ),
            )

            assertEquals(
                listOf(true, false, true),
                useCase().toList(),
            )
        }
}

private class FakeInternetConnectionRepository(
    private val availability: Flow<Boolean>,
) : InternetConnectionRepository.Local {
    override fun observeAvailability(): Flow<Boolean> = availability
}
