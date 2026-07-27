package com.cinemateca.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.cinemateca.features.designsystem.childTestId
import com.cinemateca.features.designsystem.testId
import com.cinemateca.features.trailers.details.TrailerDetailsDestination
import com.cinemateca.features.trailers.home.HomeDestination

@Composable
fun CinematecaApp(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    testId: String? = null,
) {
    AppNavHost(
        navController = navController,
        modifier = modifier,
        testId = testId,
    )
}

@Composable
fun AppNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier,
    testId: String? = null,
) {
    NavHost(
        navController = navController,
        startDestination = HomeRoute,
        modifier = modifier.testId(testId),
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
                testId = testId.childTestId("home"),
            )
        }
        composable<TrailerDetailsRoute> {
            TrailerDetailsDestination(
                onNavigateBack = navController::navigateBackFromTrailerDetails,
                testId = testId.childTestId("details"),
            )
        }
    }
}

internal fun NavHostController.navigateBackFromTrailerDetails() {
    popBackStack<HomeRoute>(inclusive = false)
}
