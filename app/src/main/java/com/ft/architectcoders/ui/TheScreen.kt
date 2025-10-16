package com.ft.architectcoders.ui

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.ft.architectcoders.ui.theme.CineVerseTheme

@Composable
fun TheScreen(content: @Composable () -> Unit) {
    CineVerseTheme(
        darkTheme = true,
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            content = content,
        )
    }
}
