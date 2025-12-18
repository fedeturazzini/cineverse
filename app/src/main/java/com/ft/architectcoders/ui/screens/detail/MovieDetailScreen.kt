package com.ft.architectcoders.ui.screens.detail

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.ui.common.LoadingIndicator
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.IndigoDark80
import com.ft.architectcoders.ui.theme.StarBright
import kotlin.math.floor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MovieDetailScreen(
    viewModel: MovieDetailViewModel,
    onBack: () -> Unit,
) {
    val state by viewModel.state.collectAsState()

    val movieDetailState = rememberMovieDetailState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(state.movie?.title ?: "") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "",
                        )
                    }
                },
                colors =
                    TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.surface,
                        titleContentColor = MaterialTheme.colorScheme.onSurface,
                    ),
            )
        },
        floatingActionButton = {
            val favorite = state.movie?.favorite ?: false
            FloatingActionButton(onClick = {
                viewModel.onFavoriteClick()
            }) {
                Icon(
                    imageVector = if (favorite) Icons.Filled.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = stringResource(R.string.fav_button),
                )
            }
        },
        snackbarHost = {
            androidx.compose.material3.SnackbarHost(hostState = movieDetailState.snackbarHostState)
        },
    ) { padding ->
        if (state.isLoadingMovie) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                LoadingIndicator()
            }
        } else {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .verticalScroll(rememberScrollState()),
            ) {
                state.movie?.let {
                    AsyncImage(
                        model = it.backdrop,
                        contentDescription = state.movie?.title,
                        contentScale = ContentScale.Crop,
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .height(400.dp),
                    )

                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = it.originalTitle,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                        )

                        Text(
                            text = stringResource(R.string.release_date, it.releaseDate),
                            style = MaterialTheme.typography.titleSmall,
                        )

                        Text(
                            text = stringResource(R.string.synopsis),
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.primary,
                        )

                        Text(
                            text = it.overview,
                            style = MaterialTheme.typography.bodyLarge,
                        )

                        if (state.cast.isNotEmpty()) {
                            CastCarousel(cast = state.cast)
                        }

                        when (val aiState = state.aiReviewState) {
                            is AiReviewUiState.Success -> {
                                AiReviewCard(aiReview = aiState.aiReview)
                            }
                            is AiReviewUiState.Loading -> {
                                AiReviewLoadingCard()
                            }
                            is AiReviewUiState.Error -> {
                                AiReviewErrorCard()
                            }
                            is AiReviewUiState.NotRequested -> {
                                // do nothing por ahora
                            }
                        }

                        if (state.videos.isNotEmpty()) {
                            TrailersSection(videos = state.videos)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun AiReviewCard(
    aiReview: AiReview,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .size(32.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush =
                                        Brush.linearGradient(
                                            colors =
                                                listOf(
                                                    IndigoDark80,
                                                    CinemaOrange,
                                                ),
                                        ),
                                ),
                        contentAlignment = Alignment.Center,
                    ) {
                        Text(
                            text = "✨",
                            style = MaterialTheme.typography.titleMedium,
                        )
                    }

                    Text(
                        text = stringResource(R.string.ai_review),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                }
            }

            RatingStars(rating = aiReview.rating)

            Text(
                text = "\"${aiReview.quote}\"",
                style = MaterialTheme.typography.bodyLarge,
                fontStyle = FontStyle.Italic,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f),
            )
        }
    }
}

@Composable
fun AiReviewErrorCard(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f),
            ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(32.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                brush =
                                    Brush.linearGradient(
                                        colors =
                                            listOf(
                                                IndigoDark80,
                                                CinemaOrange,
                                            ),
                                    ),
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Text(
                        text = "✨",
                        style = MaterialTheme.typography.titleMedium,
                    )
                }

                Text(
                    text = stringResource(R.string.ai_review_premium),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }

            Text(
                text = stringResource(R.string.ai_review_premium_description),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
            )

            // TODO: ver que hacer con esto
            Text(
                text = stringResource(R.string.view_premium_plans),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun AiReviewLoadingCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors =
            CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
            ),
    ) {
        Row(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            LoadingIndicator(modifier = Modifier.size(24.dp))
            Text(
                text = stringResource(R.string.generating_ai_review),
                style = MaterialTheme.typography.bodyMedium,
            )
        }
    }
}

@SuppressLint("DefaultLocale")
@Composable
fun RatingStars(
    rating: Float,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        val fullStars = floor(rating).toInt()

        repeat(5) { index ->
            Icon(
                imageVector = if (index < fullStars) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = if (index < fullStars) StarBright else MaterialTheme.colorScheme.outline,
                modifier = Modifier.size(24.dp),
            )
        }

        Text(
            text = String.format("%.1f", rating),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = StarBright,
            modifier = Modifier.padding(start = 8.dp),
        )
    }
}
