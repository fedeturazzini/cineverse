package com.ft.architectcoders.ui.common.bottombar
import android.R.attr.shape
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.BlurredEdgeTreatment
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.PathMeasure
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import dev.chrisbanes.haze.HazeState
import dev.chrisbanes.haze.hazeEffect
import dev.chrisbanes.haze.HazeStyle
import dev.chrisbanes.haze.hazeChild
import dev.chrisbanes.haze.hazeEffect

@Composable
fun GlassmorphicBottomBar(
    hazeState: HazeState,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier =
            modifier
                .padding(vertical = 24.dp, horizontal = 64.dp)
                .fillMaxWidth()
                .height(64.dp)
                .clip(CircleShape)
                .hazeEffect(state = hazeState) {
                    blurRadius = 30.dp
                }
                .border(
                    width = Dp.Hairline,
                    brush =
                        Brush.verticalGradient(
                            colors =
                                listOf(
                                    Color.White.copy(alpha = .8f),
                                    Color.White.copy(alpha = .2f),
                                ),
                        ),
                    shape = CircleShape,
                ),
    ) {
        BottomBarTabs(
            tabs = bottomBarTabs,
            selectedTab = selectedTabIndex,
            onTabSelected = { tab ->
                onTabSelected(bottomBarTabs.indexOf(tab))
            },
        )

        val animatedSelectedTabIndex by animateFloatAsState(
            targetValue = selectedTabIndex.toFloat(),
            label = "animatedSelectedTabIndex",
            animationSpec =
                spring(
                    stiffness = Spring.StiffnessLow,
                    dampingRatio = Spring.DampingRatioLowBouncy,
                ),
        )

        val animatedColor by animateColorAsState(
            targetValue = bottomBarTabs[selectedTabIndex].color,
            label = "animatedColor",
            animationSpec = spring(stiffness = Spring.StiffnessLow),
        )

        // Glow indicator
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape)
                    .blur(50.dp, edgeTreatment = BlurredEdgeTreatment.Unbounded),
        ) {
            val tabWidth = size.width / bottomBarTabs.size
            drawCircle(
                color = animatedColor.copy(alpha = .6f),
                radius = size.height / 2,
                center =
                    Offset(
                        (tabWidth * animatedSelectedTabIndex) + tabWidth / 2,
                        size.height / 2,
                    ),
            )
        }

        // Bottom gleam effect
        Canvas(
            modifier =
                Modifier
                    .fillMaxSize()
                    .clip(CircleShape),
        ) {
            val path =
                Path().apply {
                    addRoundRect(RoundRect(size.toRect(), CornerRadius(size.height)))
                }
            val length = PathMeasure().apply { setPath(path, false) }.length

            val tabWidth = size.width / bottomBarTabs.size
            drawPath(
                path,
                brush =
                    Brush.horizontalGradient(
                        colors =
                            listOf(
                                animatedColor.copy(alpha = 0f),
                                animatedColor.copy(alpha = 1f),
                                animatedColor.copy(alpha = 1f),
                                animatedColor.copy(alpha = 0f),
                            ),
                        startX = tabWidth * animatedSelectedTabIndex,
                        endX = tabWidth * (animatedSelectedTabIndex + 1),
                    ),
                style =
                    Stroke(
                        width = 6f,
                        pathEffect =
                            PathEffect.dashPathEffect(
                                intervals = floatArrayOf(length / 2, length),
                            ),
                    ),
            )
        }
    }
}
