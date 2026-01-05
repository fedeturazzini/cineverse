package com.ft.architectcoders.ui.screens.duel

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.ui.theme.StarBright
import kotlin.math.abs
import kotlin.math.roundToInt

@Composable
fun DuelMovieCard(
    movie: Movie,
    label: String,
    labelColor: Color,
    onSwipeComplete: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec = tween(durationMillis = 100),
        label = "cardOffset",
    )
    val rotation = (animatedOffsetX / 20f).coerceIn(-15f, 15f)
    val swipeThreshold = 300f

    val cardShape = RoundedCornerShape(24.dp)

    Card(
        modifier =
            modifier
                .fillMaxWidth()
                .height(420.dp)
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .rotate(rotation)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (kotlin.math.abs(offsetX) > swipeThreshold) {
                                onSwipeComplete()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                    ) { _, dragAmount ->
                        offsetX += dragAmount.x
                    }
                },
        shape = cardShape,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp,
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.poster,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(cardShape),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.85f),
                                        ),
                                    startY = 200f,
                                ),
                        ),
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            color = labelColor.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = movie.releaseDate.take(4),
                    style = MaterialTheme.typography.bodyLarge,
                    color = StarBright,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
fun DuelMovieCardSimple(
    movie: Movie,
    label: String,
    labelColor: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val cardShape = RoundedCornerShape(24.dp)

    Card(
        onClick = onClick,
        modifier =
            modifier
                .fillMaxWidth()
                .height(420.dp),
        shape = cardShape,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 12.dp,
                pressedElevation = 16.dp,
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = movie.poster,
                contentDescription = movie.title,
                contentScale = ContentScale.Crop,
                modifier =
                    Modifier
                        .fillMaxSize()
                        .clip(cardShape),
            )

            Box(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .background(
                            brush =
                                Brush.verticalGradient(
                                    colors =
                                        listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.3f),
                                            Color.Black.copy(alpha = 0.85f),
                                        ),
                                    startY = 200f,
                                ),
                        ),
            )

            Box(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(16.dp)
                        .background(
                            color = labelColor.copy(alpha = 0.9f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .border(
                            width = 1.dp,
                            color = Color.White.copy(alpha = 0.3f),
                            shape = RoundedCornerShape(12.dp),
                        )
                        .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                )
            }

            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                Text(
                    text = movie.releaseDate.take(4),
                    style = MaterialTheme.typography.bodyLarge,
                    color = StarBright,
                    fontWeight = FontWeight.Medium,
                )

                Text(
                    text = movie.overview,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.8f),
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                )
            }

            Text(
                text = "TAP PARA ELEGIR",
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White.copy(alpha = 0.6f),
                textAlign = TextAlign.Center,
                modifier =
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 8.dp),
            )
        }
    }
}

@Composable
fun DuelMovieCardVertical(
    movie: Movie,
    label: String,
    labelColor: Color,
    onSelect: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var offsetX by remember { mutableFloatStateOf(0f) }
    val animatedOffsetX by animateFloatAsState(
        targetValue = offsetX,
        animationSpec =
            spring(
                dampingRatio = Spring.DampingRatioMediumBouncy,
                stiffness = Spring.StiffnessLow,
            ),
        label = "cardOffsetX",
    )

    val rotation = (animatedOffsetX / 30f).coerceIn(-8f, 8f)
    val scale = 1f - (abs(animatedOffsetX) / 2000f).coerceIn(0f, 0.05f)
    val swipeThreshold = 150f

    val cardShape = RoundedCornerShape(20.dp)

    Card(
        modifier =
            modifier
                .offset { IntOffset(animatedOffsetX.roundToInt(), 0) }
                .rotate(rotation)
                .scale(scale)
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            if (abs(offsetX) > swipeThreshold) {
                                onSelect()
                            }
                            offsetX = 0f
                        },
                        onDragCancel = {
                            offsetX = 0f
                        },
                    ) { _, dragAmount ->
                        offsetX += dragAmount.x
                    }
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = { onSelect() },
                    )
                },
        shape = cardShape,
        elevation =
            CardDefaults.cardElevation(
                defaultElevation = 8.dp,
            ),
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Row(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = movie.poster,
                    contentDescription = movie.title,
                    contentScale = ContentScale.Crop,
                    modifier =
                        Modifier
                            .fillMaxHeight()
                            .width(130.dp)
                            .clip(RoundedCornerShape(topStart = 20.dp, bottomStart = 20.dp)),
                )

                Box(
                    modifier =
                        Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                brush =
                                    Brush.horizontalGradient(
                                        colors =
                                            listOf(
                                                Color.Black.copy(alpha = 0.9f),
                                                labelColor.copy(alpha = 0.15f),
                                            ),
                                    ),
                            ),
                ) {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(16.dp),
                        verticalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Box(
                                modifier =
                                    Modifier
                                        .background(
                                            color = labelColor,
                                            shape = RoundedCornerShape(8.dp),
                                        )
                                        .padding(horizontal = 12.dp, vertical = 4.dp),
                            ) {
                                Text(
                                    text = label,
                                    style = MaterialTheme.typography.labelLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                )
                            }

                            Text(
                                text = movie.title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = movie.releaseDate.take(4),
                                style = MaterialTheme.typography.bodyMedium,
                                color = StarBright,
                                fontWeight = FontWeight.Medium,
                            )
                        }

                        Column(
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = movie.overview,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.7f),
                                maxLines = 3,
                                overflow = TextOverflow.Ellipsis,
                            )

                            Text(
                                text = "← SWIPE O TAP →",
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = FontWeight.Medium,
                                color = labelColor.copy(alpha = 0.8f),
                                textAlign = TextAlign.Center,
                                modifier = Modifier.fillMaxWidth(),
                            )
                        }
                    }
                }
            }
        }
    }
}
