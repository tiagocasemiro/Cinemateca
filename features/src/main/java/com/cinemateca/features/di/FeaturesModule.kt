package com.cinemateca.features.di

import com.cinemateca.features.trailers.details.TrailerDetailsViewModel
import com.cinemateca.features.trailers.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featuresModule = module {
    viewModel {
        TrailerDetailsViewModel(
            savedStateHandle = get(),
            getMovieByKinoCheckIdUseCase = get(),
            observeInternetConnectionUseCase = get(),
            observeFavoriteMovieIdsUseCase = get(),
            observeWatchlistMovieIdsUseCase = get(),
            toggleFavoriteMovieUseCase = get(),
            toggleWatchlistMovieUseCase = get(),
        )
    }
    viewModel {
        HomeViewModel(
            getTrendingTrailersUseCase = get(),
            observeInternetConnectionUseCase = get(),
            observeFavoriteMovieIdsUseCase = get(),
            observeWatchlistMovieIdsUseCase = get(),
            toggleFavoriteMovieUseCase = get(),
            toggleWatchlistMovieUseCase = get(),
        )
    }
}
