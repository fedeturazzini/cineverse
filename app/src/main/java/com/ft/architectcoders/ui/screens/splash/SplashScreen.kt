package com.ft.architectcoders.ui.screens.splash

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.ft.architectcoders.ui.common.StarryBackground
import com.ft.architectcoders.ui.theme.DeepSpaceBlue20
import com.ft.architectcoders.ui.theme.IndigoDark80
import com.ft.architectcoders.ui.theme.SurfaceVariantLight

@Composable
fun SplashScreen(onAnimationEnd: () -> Unit) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("splash_animation.json"),
    )
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = 2,
        isPlaying = true,
        speed = 1.0f,
        restartOnPlay = false,
    )

    LaunchedEffect(progress) {
        if (progress == 1f) {
            onAnimationEnd()
        }
    }

    Box(
        modifier =
            Modifier
                .fillMaxSize()
                .background(
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    DeepSpaceBlue20,
                                    IndigoDark80,
                                ),
                        ),
                ),
        contentAlignment = Alignment.Center,
    ) {
        StarryBackground(
            modifier = Modifier.fillMaxSize(),
            alpha = 0.7f,
        )

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress },
                modifier = Modifier.size(200.dp),
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "CineVerse",
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                color = SurfaceVariantLight,
            )
        }
    }
}
