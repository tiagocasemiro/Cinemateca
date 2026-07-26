package com.cinemateca.di

import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByImdbIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByTmdbIdUseCase
import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.usecase.GetLatestTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import com.cinemateca.networking.di.kinoCheckNetworkingModule
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Assert.assertNotSame
import org.junit.Before
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.dsl.module

class UseCaseModuleTest {
    @Before
    fun setUp() {
        startKoin {
            modules(
                kinoCheckNetworkingModule(),
                module {
                    single<InternetConnectionRepository.Local> {
                        FakeInternetConnectionRepository()
                    }
                    single<FavoriteMovieRepository.Local> {
                        FakeFavoriteMovieRepository()
                    }
                    single<WatchlistMovieRepository.Local> {
                        FakeWatchlistMovieRepository()
                    }
                },
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
            koin.get<ObserveInternetConnectionUseCase>(),
            koin.get<ObserveInternetConnectionUseCase>(),
        )
        assertNotSame(
            koin.get<GetTrendingTrailersUseCase>(),
            koin.get<GetTrendingTrailersUseCase>(),
        )
        koin.get<GetLatestTrailersUseCase>()
        koin.get<GetTrailersUseCase>()
        koin.get<GetMovieByKinoCheckIdUseCase>()
        koin.get<GetMovieByTmdbIdUseCase>()
        koin.get<GetMovieByImdbIdUseCase>()
        assertNotSame(
            koin.get<ObserveFavoriteMovieIdsUseCase>(),
            koin.get<ObserveFavoriteMovieIdsUseCase>(),
        )
        assertNotSame(
            koin.get<ObserveWatchlistMovieIdsUseCase>(),
            koin.get<ObserveWatchlistMovieIdsUseCase>(),
        )
        koin.get<ToggleFavoriteMovieUseCase>()
        koin.get<ToggleWatchlistMovieUseCase>()
    }
}

private class FakeInternetConnectionRepository :
    InternetConnectionRepository.Local {
    override fun observeAvailability(): Flow<Boolean> = flowOf(true)
}

private class FakeFavoriteMovieRepository :
    FavoriteMovieRepository.Local {
    override fun observeMovieIds(): Flow<Set<String>> = flowOf(emptySet())

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) = Unit
}

private class FakeWatchlistMovieRepository :
    WatchlistMovieRepository.Local {
    override fun observeMovieIds(): Flow<Set<String>> = flowOf(emptySet())

    override suspend fun setSelected(
        movieId: String,
        isSelected: Boolean,
    ) = Unit
}
