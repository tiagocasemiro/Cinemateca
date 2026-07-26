package com.cinemateca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cinemateca.features.trailers.details.TrailerDetailsDestination
import com.cinemateca.features.trailers.home.HomeDestination

@Composable
fun CinematecaApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
) {
    AppNavHost(
        navController = navController,
        modifier = modifier,
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier,
    ) {
        composable<HomeRoute> {
            HomeDestination(
                onTrailerClick = { trailerId, movieId, resourceType ->
                    navController.navigate(
                        TrailerDetailsRoute(
                            movieId = movieId,
                            trailerId = trailerId,
                            resourceType = resourceType,
                        ),
                    )
                },
            )
        }
        composable<TrailerDetailsRoute> {
            TrailerDetailsDestination(
                onNavigateBack = navController::popBackStack,
            )
        }
    }
}
