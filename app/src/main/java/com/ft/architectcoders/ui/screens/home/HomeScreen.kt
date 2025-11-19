package com.ft.architectcoders.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.ui.MovieItem
import com.ft.architectcoders.ui.common.LoadingIndicator
import com.ft.architectcoders.ui.common.toFlagEmoji
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onMovieClick: (Movie) -> Unit,
    viewModel: HomeViewModel = koinViewModel(),
) {
    val homeState = rememberHomeState()

    homeState.AskRegionEffect {
        viewModel.init(region = it)
    }

    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(text = stringResource(id = R.string.app_name))

                        Text(
                            text = state.region.toFlagEmoji(),
                            fontSize = 24.sp,
                        )
                    }
                },
                scrollBehavior = homeState.scrollBehavior,
            )
        },
        modifier = Modifier.nestedScroll(homeState.scrollBehavior.nestedScrollConnection),
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { padding ->

        if (state.isLoading) {
            LoadingIndicator()
        }

        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = padding,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(horizontal = 8.dp),
        ) {
            items(state.movies, key = { it.id }) { movie ->
                MovieItem(
                    movie = movie,
                    onClick = { onMovieClick(movie) },
                )
            }
        }
    }
}
