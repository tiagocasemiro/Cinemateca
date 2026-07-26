package com.cinemateca.features.di

import com.cinemateca.features.trailers.home.HomeViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val featuresModule = module {
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
