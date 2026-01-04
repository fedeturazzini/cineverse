package com.ft.architectcoders.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ft.architectcoders.ui.common.bottombar.GlassmorphicBottomBar
import com.ft.architectcoders.ui.common.bottombar.bottomBarTabs
import com.ft.architectcoders.ui.screens.detail.MovieDetailScreen
import com.ft.architectcoders.ui.screens.detail.MovieDetailViewModel
import com.ft.architectcoders.ui.screens.duel.DuelScreen
import com.ft.architectcoders.ui.screens.foryou.ForYouExperience
import com.ft.architectcoders.ui.screens.foryou.ForYouScreen
import com.ft.architectcoders.ui.screens.home.HomeScreen
import com.ft.architectcoders.ui.screens.profile.ProfileScreen
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeSource
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

sealed class NavScreen(val route: String) {
    data object Home : NavScreen("home")

    data object Detail : NavScreen("detail/{${NavArgs.MovieId.key}}") {
        fun createRoute(movieId: Int) = "detail/$movieId"
    }

    data object Profile : NavScreen("profile")

    data object ForYou : NavScreen("for_you")

    data object Duel : NavScreen("duel")
}

enum class NavArgs(val key: String) {
    MovieId("movieId"),
}

private val bottomBarRoutes =
    listOf(
        NavScreen.Home.route,
        NavScreen.Profile.route,
        NavScreen.ForYou.route,
    )

@Composable
fun Navigation() {
    val navController = rememberNavController()
    val hazeState = remember { HazeState() }

    val navBackStackEntry by navController.currentBackStackEntryAsState()

    val currentRoute = navBackStackEntry?.destination?.route
    val showBottomBar = currentRoute in bottomBarRoutes

    val selectedTabIndex =
        when (currentRoute) {
            NavScreen.Profile.route -> 0
            NavScreen.Home.route -> 1
            NavScreen.ForYou.route -> 2
            else -> 1
        }

    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                GlassmorphicBottomBar(
                    hazeState = hazeState,
                    selectedTabIndex = selectedTabIndex,
                    onTabSelected = { index ->
                        val route = bottomBarTabs[index].route
                        navController.navigate(route) {
                            popUpTo(NavScreen.Home.route) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                )
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = NavScreen.Home.route,
            modifier =
                Modifier
                    .fillMaxSize()
                    .hazeSource(state = hazeState) { },
        ) {
            composable(NavScreen.Profile.route) {
                ProfileScreen(contentPadding = padding)
            }

            composable(NavScreen.Home.route) {
                HomeScreen(
                    contentPadding = padding,
                    onMovieClick = { movie ->
                        navController.navigate(NavScreen.Detail.createRoute(movie.id))
                    },
                )
            }

            composable(
                route = NavScreen.Detail.route,
                arguments = listOf(navArgument(NavArgs.MovieId.key) { type = NavType.IntType }),
            ) { backStackEntry ->
                val movieId = requireNotNull(backStackEntry.arguments?.getInt(NavArgs.MovieId.key))
                val viewModel: MovieDetailViewModel = koinViewModel { parametersOf(movieId) }

                MovieDetailScreen(
                    viewModel = viewModel,
                    onBack = { navController.popBackStack() },
                )
            }

            composable(NavScreen.ForYou.route) {
                ForYouScreen(
                    contentPadding = padding,
                    onExperienceClick = { experience ->
                        when (experience) {
                            ForYouExperience.MOVIE_DUEL -> {
                                navController.navigate(NavScreen.Duel.route)
                            }
                            else -> { /* Not implemented yet */ }
                        }
                    },
                )
            }

            composable(NavScreen.Duel.route) {
                DuelScreen(
                    onBack = { navController.popBackStack() },
                    onMovieClick = { movieId ->
                        navController.navigate(NavScreen.Detail.createRoute(movieId))
                    },
                )
            }
        }
    }
}
