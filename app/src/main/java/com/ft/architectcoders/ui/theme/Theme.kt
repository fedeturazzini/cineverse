@file:Suppress("DEPRECATION")

package com.ft.architectcoders.ui.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme =
    darkColorScheme(
        primary = DeepSpaceBlue80,
        onPrimary = Color.White,
        primaryContainer = DeepSpaceBlue20,
        onPrimaryContainer = DeepSpaceBlue80,
        secondary = GalaxyPurple80,
        onSecondary = Color.White,
        secondaryContainer = GalaxyPurple20,
        onSecondaryContainer = GalaxyPurple80,
        tertiary = IndigoDark80,
        onTertiary = Color.White,
        tertiaryContainer = IndigoDark20,
        onTertiaryContainer = IndigoDark80,
        error = Color(0xFFFF5252),
        onError = Color(0xFF690005),
        errorContainer = Color(0xFF93000A),
        onErrorContainer = Color(0xFFFFDAD6),
        background = SurfaceDark,
        onBackground = Color(0xFFE3E2E6),
        surface = SurfaceDark,
        onSurface = Color(0xFFE3E2E6),
        surfaceVariant = SurfaceVariantDark,
        onSurfaceVariant = Color(0xFFC4C6D0),
        outline = Color(0xFF8E9099),
        outlineVariant = Color(0xFF44464F),
        scrim = Color.Black,
    )

private val LightColorScheme =
    lightColorScheme(
        primary = DeepSpaceBlue40,
        onPrimary = Color.White,
        primaryContainer = Color(0xFFD8E2FF),
        onPrimaryContainer = DeepSpaceBlue20,
        secondary = GalaxyPurple40,
        onSecondary = Color.White,
        secondaryContainer = Color(0xFFE8DDFF),
        onSecondaryContainer = GalaxyPurple20,
        tertiary = IndigoDark40,
        onTertiary = Color.White,
        tertiaryContainer = Color(0xFFB3F5FF),
        onTertiaryContainer = IndigoDark20,
        error = Color(0xFFBA1A1A),
        onError = Color.White,
        errorContainer = Color(0xFFFFDAD6),
        onErrorContainer = Color(0xFF410002),
        background = SurfaceLight,
        onBackground = Color(0xFF1A1C1E),
        surface = SurfaceLight,
        onSurface = Color(0xFF1A1C1E),
        surfaceVariant = SurfaceVariantLight,
        onSurfaceVariant = Color(0xFF44464F),
        outline = Color(0xFF74777F),
        outlineVariant = Color(0xFFC4C6D0),
        scrim = Color.Black,
    )

@Composable
fun CineVerseTheme(
    darkTheme: Boolean = true,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme =
        when {
            dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
                val context = LocalContext.current
                if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
            }
            darkTheme -> DarkColorScheme
            else -> LightColorScheme
        }

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
