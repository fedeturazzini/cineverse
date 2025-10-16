package com.ft.architectcoders.ui

import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.ft.architectcoders.ui.screens.home.HomeScreen
import com.ft.architectcoders.ui.screens.detail.MovieDetailScreen
import com.ft.architectcoders.ui.screens.detail.MovieDetailViewModel
import org.koin.androidx.compose.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            HomeScreen(onMovieClick = { movie ->
                navController.navigate("detail/${movie.id}")
            })
        }

        composable(
            route = "detail/{movieId}",
            arguments = listOf(navArgument("movieId") { type = NavType.IntType }),
        ) { backStackEntry ->
            val movieId = requireNotNull(backStackEntry.arguments?.getInt("movieId"))

            val viewModel: MovieDetailViewModel = koinViewModel { parametersOf(movieId) }

            MovieDetailScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
