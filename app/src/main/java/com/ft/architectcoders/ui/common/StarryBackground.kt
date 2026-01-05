package com.ft.architectcoders.ui.common

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.layout.ContentScale
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.rememberLottieComposition

@Composable
fun StarryBackground(
    modifier: Modifier = Modifier,
    alpha: Float = 0.6f,
) {
    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("stars_parallax.json"),
    )

    LottieAnimation(
        composition = composition,
        iterations = LottieConstants.IterateForever,
        modifier =
            modifier
                .fillMaxSize()
                .alpha(alpha),
        contentScale = ContentScale.Crop,
    )
}
