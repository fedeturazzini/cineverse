package com.ft.architectcoders.ui.screens.challenge

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.ui.common.LoadingIndicator
import com.ft.architectcoders.ui.common.StarryBackground
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.GalaxyPurple40
import com.ft.architectcoders.ui.theme.GalaxyPurple80
import com.ft.architectcoders.ui.theme.SpaceNavy
import com.ft.architectcoders.ui.theme.StarBright
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DailyChallengeScreen(
    onBack: () -> Unit,
    onMovieClick: (Int) -> Unit,
    viewModel: DailyChallengeViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is DailyChallengeEvent.NavigateToMovieDetail -> {
                    onMovieClick(event.movieId)
                }
                is DailyChallengeEvent.ChallengeCompleted -> {
                    snackbarHostState.showSnackbar(
                        "¡Desbloqueaste: ${event.badgeEmoji} ${event.badgeName}!",
                    )
                }
                is DailyChallengeEvent.ChallengeFailed -> {
                    val correctMovies = event.correctMovies.joinToString(", ")
                    snackbarHostState.showSnackbar(
                        "¡Ups! Las correctas eran: $correctMovies",
                    )
                }
                is DailyChallengeEvent.ShowError -> {
                    snackbarHostState.showSnackbar(event.message)
                }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.challenge_title),
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(R.string.close),
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                ),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
                is DailyChallengeUiState.Loading -> LoadingContent(padding)
                is DailyChallengeUiState.Success -> SuccessContent(
                    state = currentState,
                    padding = padding,
                    onMovieClick = { viewModel.onMovieClick(it) },
                    onCompleteChallenge = { viewModel.onCompleteChallenge(it) },
                    onDismissCelebration = { viewModel.onDismissCelebration() },
                )
                is DailyChallengeUiState.Error -> ErrorContent(
                    message = currentState.message,
                    padding = padding,
                    onRetry = { viewModel.onRetry() },
                )
            }
        }
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
            LoadingIndicator(modifier = Modifier.size(120.dp))
            Text(
                text = stringResource(R.string.challenge_loading),
                style = MaterialTheme.typography.titleMedium,
                color = Color.White,
            )
        }
    }
}

@Composable
private fun SuccessContent(
    state: DailyChallengeUiState.Success,
    padding: PaddingValues,
    onMovieClick: (ChallengeMovieOption) -> Unit,
    onCompleteChallenge: (ChallengeMovieOption) -> Unit,
    onDismissCelebration: () -> Unit,
) {
    val challenge = state.challenge
    val isCompleted = challenge.status == ChallengeStatus.COMPLETED
    val isFailed = challenge.status == ChallengeStatus.FAILED
    val isFinished = isCompleted || isFailed
    val progress by animateFloatAsState(
        targetValue = if (isCompleted) 1f else 0f,
        label = "progress",
    )

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                ChallengeHeader(
                    challenge = challenge,
                    progress = progress,
                    isCompleted = isCompleted,
                    isFailed = isFailed,
                )
            }

            item {
                Text(
                    text = when {
                        isFailed -> stringResource(R.string.challenge_movies_failed_title)
                        isCompleted -> stringResource(R.string.challenge_movies_completed_title)
                        else -> stringResource(R.string.challenge_movies_title)
                    },
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.padding(vertical = 8.dp),
                )
            }

            itemsIndexed(
                items = challenge.movieOptions,
                key = { _, movie -> movie.movieId },
            ) { index, movie ->
                ChallengeMovieCard(
                    movie = movie,
                    index = index + 1,
                    isFinished = isFinished,
                    isSelectedMovie = challenge.completedMovieId == movie.movieId,
                    isCompleting = state.isCompleting,
                    onClick = { onMovieClick(movie) },
                    onComplete = { onCompleteChallenge(movie) },
                )
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }

        AnimatedVisibility(
            visible = state.showCompletionCelebration,
            enter = fadeIn() + scaleIn(),
            exit = fadeOut() + scaleOut(),
            modifier = Modifier.fillMaxSize(),
        ) {
            CelebrationOverlay(
                badge = challenge.badge,
                onDismiss = onDismissCelebration,
            )
        }
    }
}

@Composable
private fun ChallengeHeader(
    challenge: DailyChallenge,
    progress: Float,
    isCompleted: Boolean,
    isFailed: Boolean,
) {
    val isFinished = isCompleted || isFailed
    val headerColor = when {
        isCompleted -> Color.Green.copy(alpha = 0.15f)
        isFailed -> Color.Red.copy(alpha = 0.15f)
        else -> GalaxyPurple80.copy(alpha = 0.4f)
    }
    val accentColor = when {
        isCompleted -> Color.Green
        isFailed -> Color.Red
        else -> CinemaOrange
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = headerColor),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Surface(
                    shape = CircleShape,
                    color = accentColor.copy(alpha = 0.2f),
                ) {
                    Icon(
                        imageVector = when {
                            isCompleted -> Icons.Default.CheckCircle
                            isFailed -> Icons.Default.Close
                            else -> Icons.Default.AutoAwesome
                        },
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier
                            .padding(12.dp)
                            .size(28.dp),
                    )
                }
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isFailed -> stringResource(R.string.challenge_failed_header)
                            isCompleted -> stringResource(R.string.challenge_completed_header)
                            else -> challenge.title
                        },
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                    )
                    Text(
                        text = when {
                            isFailed -> stringResource(R.string.challenge_failed_subtitle)
                            isCompleted -> stringResource(R.string.challenge_come_back_tomorrow)
                            else -> challenge.reason
                        },
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f),
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        text = stringResource(R.string.challenge_progress),
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                    Text(
                        text = when {
                            isCompleted -> "1/1"
                            isFailed -> "0/1"
                            else -> "0/1"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = when {
                            isCompleted -> Color.Green
                            isFailed -> Color.Red
                            else -> StarBright
                        },
                    )
                }
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp)),
                    color = accentColor,
                    trackColor = Color.White.copy(alpha = 0.2f),
                )
            }


            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = when {
                            isCompleted -> Color.Green.copy(alpha = 0.15f)
                            isFailed -> Color.Red.copy(alpha = 0.1f)
                            else -> Color.White.copy(alpha = 0.1f)
                        },
                        shape = RoundedCornerShape(12.dp),
                    )
                    .padding(12.dp),
            ) {
                Text(
                    text = challenge.badge.emoji,
                    fontSize = 32.sp,
                    modifier = if (isFailed) Modifier else Modifier,
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when {
                            isCompleted -> stringResource(R.string.challenge_badge_earned)
                            isFailed -> stringResource(R.string.challenge_badge_missed)
                            else -> stringResource(R.string.challenge_reward)
                        },
                        style = MaterialTheme.typography.labelSmall,
                        color = when {
                            isCompleted -> Color.Green
                            isFailed -> Color.Red.copy(alpha = 0.8f)
                            else -> Color.White.copy(alpha = 0.6f)
                        },
                    )
                    Text(
                        text = challenge.badge.name,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (isFailed) Color.White.copy(alpha = 0.5f) else if (isCompleted) Color.White else StarBright,
                    )
                }
                when {
                    isCompleted -> Icon(
                        Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color.Green,
                        modifier = Modifier.size(24.dp),
                    )
                    isFailed -> Icon(
                        Icons.Default.Close,
                        contentDescription = null,
                        tint = Color.Red,
                        modifier = Modifier.size(24.dp),
                    )
                }
            }
            if (isFinished) {
                NextChallengeCountdown()
            }

            if (!isFinished && challenge.rules.isNotEmpty()) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    challenge.rules.forEach { rule ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "•",
                                color = StarBright,
                                fontWeight = FontWeight.Bold,
                            )
                            Text(
                                text = rule,
                                style = MaterialTheme.typography.bodySmall,
                                color = Color.White.copy(alpha = 0.8f),
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun NextChallengeCountdown() {
    var timeUntilMidnight by remember { mutableStateOf(calculateTimeUntilMidnight()) }

    LaunchedEffect(Unit) {
        while (true) {
            delay(60_000L)
            timeUntilMidnight = calculateTimeUntilMidnight()
        }
    }

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = CinemaOrange.copy(alpha = 0.15f),
                shape = RoundedCornerShape(12.dp),
            )
            .padding(12.dp),
    ) {
        Icon(
            Icons.Default.AccessTime,
            contentDescription = null,
            tint = CinemaOrange,
            modifier = Modifier.size(20.dp),
        )
        Column {
            Text(
                text = stringResource(R.string.challenge_next_available),
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
            Text(
                text = timeUntilMidnight,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = CinemaOrange,
            )
        }
    }
}

private fun calculateTimeUntilMidnight(): String {
    val now = java.util.Calendar.getInstance()
    val midnight = java.util.Calendar.getInstance().apply {
        add(java.util.Calendar.DAY_OF_MONTH, 1)
        set(java.util.Calendar.HOUR_OF_DAY, 0)
        set(java.util.Calendar.MINUTE, 0)
        set(java.util.Calendar.SECOND, 0)
        set(java.util.Calendar.MILLISECOND, 0)
    }

    val diffMillis = midnight.timeInMillis - now.timeInMillis
    val hours = (diffMillis / (1000 * 60 * 60)).toInt()
    val minutes = ((diffMillis / (1000 * 60)) % 60).toInt()

    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        minutes > 0 -> "${minutes}m"
        else -> "¡Pronto!"
    }
}

@Composable
private fun ChallengeMovieCard(
    movie: ChallengeMovieOption,
    index: Int,
    isFinished: Boolean,
    isSelectedMovie: Boolean,
    isCompleting: Boolean,
    onClick: () -> Unit,
    onComplete: () -> Unit,
) {
    val cardColor = when {
        isFinished && isSelectedMovie && movie.isCorrectChoice -> Color.Green.copy(alpha = 0.2f)
        isFinished && isSelectedMovie && !movie.isCorrectChoice -> Color.Red.copy(alpha = 0.2f)
        isFinished && movie.isCorrectChoice -> Color.Green.copy(alpha = 0.1f)
        isFinished && !movie.isCorrectChoice -> Color.White.copy(alpha = 0.05f)
        else -> GalaxyPurple40.copy(alpha = 0.5f)
    }

    val circleColor = when {
        isFinished && movie.isCorrectChoice -> Color.Green
        isFinished && !movie.isCorrectChoice -> Color.Red.copy(alpha = 0.5f)
        else -> CinemaOrange
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = cardColor),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Surface(
                shape = CircleShape,
                color = circleColor,
                modifier = Modifier.size(32.dp),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    when {
                        isFinished && movie.isCorrectChoice -> Icon(
                            Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                        isFinished && !movie.isCorrectChoice -> Icon(
                            Icons.Default.Close,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(20.dp),
                        )
                        else -> Text(
                            text = index.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            AsyncImage(
                model = movie.poster,
                contentDescription = movie.title,
                modifier = Modifier
                    .size(60.dp, 90.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop,
            )

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = movie.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )

                movie.year?.let { year ->
                    Text(
                        text = year,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.7f),
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                if (isFinished) {
                    Text(
                        text = if (movie.isCorrectChoice) {
                            "✓ Correcta"
                        } else {
                            "✗ Incorrecta"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        color = if (movie.isCorrectChoice) Color.Green else Color.Red.copy(alpha = 0.8f),
                    )
                } else {
                    Text(
                        text = movie.whyItFits,
                        style = MaterialTheme.typography.bodySmall,
                        color = StarBright,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }

            if (!isFinished) {
                Spacer(modifier = Modifier.width(8.dp))
                Button(
                    onClick = onComplete,
                    enabled = !isCompleting,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CinemaOrange,
                    ),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                ) {
                    if (isCompleting) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = Color.White,
                            strokeWidth = 2.dp,
                        )
                    } else {
                        Icon(
                            Icons.Default.EmojiEvents,
                            contentDescription = stringResource(R.string.challenge_select_movie),
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }

            if (isFinished && isSelectedMovie) {
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (movie.isCorrectChoice) Color.Green.copy(alpha = 0.3f) else Color.Red.copy(alpha = 0.3f),
                ) {
                    Text(
                        text = stringResource(R.string.challenge_your_choice),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun CelebrationOverlay(
    badge: com.ft.architectcoders.domain.model.ChallengeBadge,
    onDismiss: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .clickable(onClick = onDismiss),
        contentAlignment = Alignment.Center,
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(
                containerColor = GalaxyPurple80,
            ),
            modifier = Modifier.padding(32.dp),
        ) {
            Column(
                modifier = Modifier.padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Icon(
                    Icons.Default.EmojiEvents,
                    contentDescription = null,
                    tint = StarBright,
                    modifier = Modifier.size(64.dp),
                )

                Text(
                    text = stringResource(R.string.challenge_completed_title),
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                )

                Text(
                    text = badge.emoji,
                    fontSize = 64.sp,
                )

                Text(
                    text = badge.name,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = StarBright,
                )

                Text(
                    text = stringResource(R.string.challenge_badge_unlocked),
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f),
                    textAlign = TextAlign.Center,
                )

                Button(
                    onClick = onDismiss,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CinemaOrange,
                    ),
                ) {
                    Text(stringResource(R.string.challenge_continue))
                }
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    padding: PaddingValues,
    onRetry: () -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp),
        ) {
            Text(
                text = stringResource(R.string.challenge_error),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.error,
                textAlign = TextAlign.Center,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
            )
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

