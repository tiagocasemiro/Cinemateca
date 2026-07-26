package com.cinemateca.di

import com.cinemateca.domain.connectivity.usecase.ObserveInternetConnectionUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByImdbIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByTmdbIdUseCase
import com.cinemateca.domain.movies.usecase.ObserveFavoriteMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ObserveWatchlistMovieIdsUseCase
import com.cinemateca.domain.movies.usecase.ToggleFavoriteMovieUseCase
import com.cinemateca.domain.movies.usecase.ToggleWatchlistMovieUseCase
import com.cinemateca.domain.trailers.usecase.GetLatestTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { ObserveInternetConnectionUseCase(repository = get()) }
    factory { GetTrendingTrailersUseCase(repository = get()) }
    factory { GetLatestTrailersUseCase(repository = get()) }
    factory { GetTrailersUseCase(repository = get()) }
    factory { GetMovieByKinoCheckIdUseCase(repository = get()) }
    factory { GetMovieByTmdbIdUseCase(repository = get()) }
    factory { GetMovieByImdbIdUseCase(repository = get()) }
    factory { ObserveFavoriteMovieIdsUseCase(repository = get()) }
    factory { ObserveWatchlistMovieIdsUseCase(repository = get()) }
    factory { ToggleFavoriteMovieUseCase(repository = get()) }
    factory { ToggleWatchlistMovieUseCase(repository = get()) }
}
