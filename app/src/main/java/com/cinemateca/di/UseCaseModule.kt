package com.cinemateca.di

import com.cinemateca.domain.movies.usecase.GetMovieByImdbIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByKinoCheckIdUseCase
import com.cinemateca.domain.movies.usecase.GetMovieByTmdbIdUseCase
import com.cinemateca.domain.trailers.usecase.GetLatestTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrailersUseCase
import com.cinemateca.domain.trailers.usecase.GetTrendingTrailersUseCase
import org.koin.dsl.module

val useCaseModule = module {
    factory { GetTrendingTrailersUseCase(repository = get()) }
    factory { GetLatestTrailersUseCase(repository = get()) }
    factory { GetTrailersUseCase(repository = get()) }
    factory { GetMovieByKinoCheckIdUseCase(repository = get()) }
    factory { GetMovieByTmdbIdUseCase(repository = get()) }
    factory { GetMovieByImdbIdUseCase(repository = get()) }
}
