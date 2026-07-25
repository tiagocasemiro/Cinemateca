package com.cinemateca.di

import com.cinemateca.domain.movies.usecase.GetMovieByImdbIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByTmdbIdUseCase
import com.cinemateca.domain.trailers.usecase.GetLatestTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import com.cinemateca.networking.di.kinoCheckNetworkingModule
import org.junit.After
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin

class UseCaseModuleTest {
    @Before
    fun setUp() {
        startKoin {
            modules(
                kinoCheckNetworkingModule(),
                useCaseModule,
            )
        }
    }

    @After
    fun tearDown() {
        stopKoin()
    }

    @Test
    fun `all use cases are available as factories for view models`() {
        val koin = org.koin.core.context.GlobalContext.get()

        assertNotSame(
            koin.get<GetTrendingTrailersUseCase>(),
            koin.get<GetTrendingTrailersUseCase>(),
        )
        koin.get<GetLatestTrailersUseCase>()
        koin.get<GetTrailersUseCase>()
        koin.get<GetMovieByKinoCheckIdUseCase>()
        koin.get<GetMovieByTmdbIdUseCase>()
        koin.get<GetMovieByImdbIdUseCase>()
    }
}
