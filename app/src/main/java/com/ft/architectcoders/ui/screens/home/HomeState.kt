package com.ft.architectcoders.ui.screens.home

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

class HomeState
    @OptIn(ExperimentalMaterial3Api::class)
    constructor(
        val scrollBehavior: TopAppBarScrollBehavior,
    )

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun rememberHomeState(scrollBehavior: TopAppBarScrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()): HomeState {
    return remember {
        HomeState(scrollBehavior)
    }
}
