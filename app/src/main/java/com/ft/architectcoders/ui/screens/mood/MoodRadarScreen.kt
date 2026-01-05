package com.ft.architectcoders.ui.screens.mood

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.ui.MovieItem
import com.ft.architectcoders.ui.common.LoadingIndicator
import com.ft.architectcoders.ui.common.StarryBackground
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.GalaxyPurple40
import com.ft.architectcoders.ui.theme.GalaxyPurple80
import com.ft.architectcoders.ui.theme.SpaceNavy
import com.ft.architectcoders.ui.theme.StarBright
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MoodRadarScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: MoodRadarViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.mood_radar_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        val isSuccess = state is MoodRadarUiState.Success
                        Icon(
                            imageVector = if (isSuccess) Icons.Default.Close else Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(if (isSuccess) R.string.close else R.string.back),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        containerColor = Color.Transparent,
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(SpaceNavy),
        ) {
            StarryBackground(
                modifier = Modifier.fillMaxSize(),
                alpha = 0.5f,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SpaceNavy.copy(alpha = 0.6f),
                                GalaxyPurple40.copy(alpha = 0.3f),
                                SpaceNavy.copy(alpha = 0.8f),
                            ),
                        ),
                    ),
            )

            when (val currentState = state) {
                is MoodRadarUiState.Idle -> {
                    MoodInputContent(
                        moodVector = currentState.moodVector,
                        onMoodChange = viewModel::onMoodChange,
                        onBuildNight = viewModel::onBuildNight,
                        padding = padding,
                    )
                }
                is MoodRadarUiState.Loading -> {
                    LoadingContent(padding = padding)
                }
                is MoodRadarUiState.Success -> {
                    SuccessContent(
                        state = currentState,
                        onMovieClick = onMovieClick,
                        onBuildNight = viewModel::onBuildNight,
                        padding = padding,
                    )
                }
                is MoodRadarUiState.Error -> {
                    ErrorContent(
                        state = currentState,
                        onRetry = viewModel::onRetry,
                        padding = padding,
                    )
                }
            }
        }
    }
}

@Composable
private fun MoodInputContent(
    moodVector: MoodVector,
    onMoodChange: (MoodAxis, Int) -> Unit,
    onBuildNight: () -> Unit,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(horizontal = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = stringResource(R.string.mood_radar_subtitle),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.height(32.dp))

        MoodSlider(
            label = stringResource(R.string.mood_energy),
            value = moodVector.energy,
            onValueChange = { onMoodChange(MoodAxis.ENERGY, it) },
            icon = Icons.Default.Bolt,
            color = CinemaOrange,
        )

        MoodSlider(
            label = stringResource(R.string.mood_humor),
            value = moodVector.humor,
            onValueChange = { onMoodChange(MoodAxis.HUMOR, it) },
            icon = Icons.Default.EmojiEmotions,
            color = StarBright,
        )

        MoodSlider(
            label = stringResource(R.string.mood_tension),
            value = moodVector.tension,
            onValueChange = { onMoodChange(MoodAxis.TENSION, it) },
            icon = Icons.Default.Warning,
            color = Color(0xFFE53935),
        )

        MoodSlider(
            label = stringResource(R.string.mood_romance),
            value = moodVector.romance,
            onValueChange = { onMoodChange(MoodAxis.ROMANCE, it) },
            icon = Icons.Default.Favorite,
            color = Color(0xFFE91E63),
        )

        MoodSlider(
            label = stringResource(R.string.mood_cerebral),
            value = moodVector.cerebral,
            onValueChange = { onMoodChange(MoodAxis.CEREBRAL, it) },
            icon = Icons.Default.Psychology,
            color = GalaxyPurple80,
        )

        Spacer(modifier = Modifier.weight(1f))

        Button(
            onClick = onBuildNight,
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp),
            shape = RoundedCornerShape(16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = CinemaOrange,
            ),
        ) {
            Icon(
                imageVector = Icons.Default.AutoAwesome,
                contentDescription = null,
                modifier = Modifier.size(24.dp),
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = stringResource(R.string.mood_build_night),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
private fun MoodSlider(
    label: String,
    value: Int,
    onValueChange: (Int) -> Unit,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Surface(
                shape = CircleShape,
                color = color.copy(alpha = 0.2f),
                modifier = Modifier.size(36.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = label,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                modifier = Modifier.weight(1f),
            )
            Text(
                text = "$value%",
                style = MaterialTheme.typography.bodyMedium,
                color = color,
                fontWeight = FontWeight.Bold,
            )
        }

        Slider(
            value = value.toFloat(),
            onValueChange = { onValueChange(it.toInt()) },
            valueRange = 0f..100f,
            colors = SliderDefaults.colors(
                thumbColor = color,
                activeTrackColor = color,
                inactiveTrackColor = color.copy(alpha = 0.3f),
            ),
        )
    }
}

@Composable
private fun LoadingContent(padding: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            LoadingIndicator()
            Text(
                text = stringResource(R.string.mood_loading),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: MoodRadarUiState.Success,
    onMovieClick: (Int) -> Unit,
    onBuildNight: () -> Unit,
    padding: PaddingValues,
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        contentPadding = PaddingValues(
            top = padding.calculateTopPadding() + 16.dp,
            bottom = 16.dp,
            start = 16.dp,
            end = 16.dp,
        ),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        item(span = { GridItemSpan(2) }) {
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically(),
            ) {
                MicroCopyCard(
                    microCopy = state.profile.microCopy,
                    explanations = state.profile.globalExplanation,
                    generatedByAi = state.profile.generatedByAi,
                )
            }
        }

        items(state.movies, key = { it.id }) { movie ->
            MovieItem(
                movie = movie,
                onClick = { onMovieClick(movie.id) },
            )
        }

        item(span = { GridItemSpan(2) }) {
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onBuildNight,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GalaxyPurple80,
                ),
            ) {
                Text(
                    text = stringResource(R.string.mood_try_again),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
private fun MicroCopyCard(
    microCopy: String,
    explanations: List<String>,
    generatedByAi: Boolean,
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GalaxyPurple40.copy(alpha = 0.6f),
        ),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(
                    imageVector = Icons.Default.AutoAwesome,
                    contentDescription = null,
                    tint = StarBright,
                    modifier = Modifier.size(24.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (generatedByAi) {
                        stringResource(R.string.generated_by_ai)
                    } else {
                        stringResource(R.string.generated_locally)
                    },
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.White.copy(alpha = 0.7f),
                )
            }

            Text(
                text = microCopy,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            explanations.forEach { explanation ->
                Row(
                    verticalAlignment = Alignment.Top,
                ) {
                    Text(
                        text = "•",
                        color = CinemaOrange,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                    Text(
                        text = explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.9f),
                    )
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    state: MoodRadarUiState.Error,
    onRetry: () -> Unit,
    padding: PaddingValues,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = null,
                tint = CinemaOrange,
                modifier = Modifier.size(64.dp),
            )

            Text(
                text = stringResource(R.string.mood_error_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Color.White,
            )

            Text(
                text = state.message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = CinemaOrange,
                ),
            ) {
                Text(
                    text = stringResource(R.string.retry),
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

