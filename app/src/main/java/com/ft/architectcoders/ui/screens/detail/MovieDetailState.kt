package com.ft.architectcoders.ui.screens.detail

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember

class MovieDetailState(
    val snackbarHostState: SnackbarHostState,
) {
    @Composable
    fun ShowMessageEffect(
        message: String?,
        onMessageShown: () -> Unit,
    ) {
        LaunchedEffect(message) {
            message?.let {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(it)
                onMessageShown()
            }
        }
    }
}

@Composable
fun rememberMovieDetailState(snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }): MovieDetailState {
    return remember {
        MovieDetailState(snackbarHostState)
    }
}
