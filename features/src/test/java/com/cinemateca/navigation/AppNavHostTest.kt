package com.cinemateca.navigation

import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.navigation.NavHostController
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.composable
import androidx.navigation.createGraph
import androidx.navigation.testing.TestNavHostController
import androidx.test.core.app.ApplicationProvider
import com.cinemateca.features.designsystem.CinematecaTheme
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class AppNavHostTest {
    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun `two fast details back clicks keep home on the back stack`() {
        val navController = createNavController()
        navController.navigate(
            TrailerDetailsRoute(
                movieId = "movie",
                trailerId = "trailer",
                resourceType = "movie",
            ),
        )

        composeRule.setContent {
            CinematecaTheme {
                TestBackButton(
                    onClick = navController::navigateBackFromTrailerDetails,
                )
            }
        }

        composeRule
            .onNodeWithTag(BACK_BUTTON_ID)
            .performClick()
            .performClick()

        assertTrue(
            navController.currentDestination?.route
                ?.startsWith(HomeRoute::class.qualifiedName.orEmpty()) == true,
        )
    }

    @Composable
    private fun TestBackButton(onClick: () -> Unit) {
        Button(
            onClick = onClick,
            modifier = Modifier.testTag(BACK_BUTTON_ID),
        ) {
            Text("Back")
        }
    }

    private fun createNavController(): NavHostController {
        return TestNavHostController(
            ApplicationProvider.getApplicationContext(),
        ).apply {
            navigatorProvider.addNavigator(ComposeNavigator())
            graph = createGraph(startDestination = HomeRoute) {
                composable<HomeRoute> {}
                composable<TrailerDetailsRoute> {}
            }
        }
    }

    private companion object {
        const val BACK_BUTTON_ID = "details_back_button"
    }
}
