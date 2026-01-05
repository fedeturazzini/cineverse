package com.ft.architectcoders.ui.screens.wrap

import android.content.Intent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.ui.common.LoadingIndicator
import com.ft.architectcoders.ui.common.StarryBackground
import com.ft.architectcoders.ui.screens.wrap.components.AiSearchHighlightsContent
import com.ft.architectcoders.ui.screens.wrap.components.ArchetypeCard
import com.ft.architectcoders.ui.screens.wrap.components.FunnyProfileSummaryCard
import com.ft.architectcoders.ui.screens.wrap.components.GenreChips
import com.ft.architectcoders.ui.screens.wrap.components.PersonRow
import com.ft.architectcoders.ui.screens.wrap.components.TopMoviesRow
import com.ft.architectcoders.ui.screens.wrap.components.WrapSectionCard
import com.ft.architectcoders.ui.screens.wrap.components.WrapShareButton
import com.ft.architectcoders.ui.screens.wrap.components.WrapStatsRow
import com.ft.architectcoders.ui.screens.wrap.components.WrapSuggestionItem
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.GalaxyPurple40
import com.ft.architectcoders.ui.theme.SpaceNavy
import com.ft.architectcoders.ui.theme.StarBright
import kotlinx.coroutines.delay
import org.koin.androidx.compose.koinViewModel

@Composable
fun CineverseWrapScreen(
    onBack: () -> Unit,
    viewModel: CineverseWrapViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.events.collect { event ->
            when (event) {
                is CineverseWrapEvent.ShareText -> {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_TEXT, event.text)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    context.startActivity(shareIntent)
                }
                is CineverseWrapEvent.ShowError -> { }
            }
        }
    }

    CineverseWrapContent(
        state = state,
        onBack = onBack,
        onShareClick = viewModel::onShareClick,
        onRetry = viewModel::onRetry,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CineverseWrapContent(
    state: CineverseWrapUiState,
    onBack: () -> Unit,
    onShareClick: () -> Unit,
    onRetry: () -> Unit,
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.wrap_title),
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                        )
                    }
                },
                actions = {
                    if (state is CineverseWrapUiState.Success) {
                        IconButton(onClick = onShareClick) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = stringResource(R.string.wrap_share),
                            )
                        }
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
                alpha = 0.7f,
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(
                                SpaceNavy.copy(alpha = 0.3f),
                                GalaxyPurple40.copy(alpha = 0.3f),
                                SpaceNavy.copy(alpha = 0.5f),
                            ),
                        ),
                    ),
            )

            when (state) {
                is CineverseWrapUiState.Loading -> LoadingContent(padding)
                is CineverseWrapUiState.Success -> SuccessContent(
                    wrap = state.wrap,
                    onShareClick = onShareClick,
                    padding = padding,
                )
                is CineverseWrapUiState.Empty -> EmptyContent(
                    interactionsNeeded = state.interactionsNeeded,
                    padding = padding,
                )
                is CineverseWrapUiState.Error -> ErrorContent(
                    message = state.message,
                    onRetry = onRetry,
                    padding = padding,
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
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            LoadingIndicator()
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(R.string.wrap_loading),
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f),
            )
        }
    }
}

@Composable
private fun SuccessContent(
    wrap: CineverseWrap,
    onShareClick: () -> Unit,
    padding: PaddingValues,
) {
    var visible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { -50 },
            ) {
                FunnyProfileSummaryCard(
                    summary = wrap.funnyProfileSummary,
                    generatedByAi = wrap.generatedByAi,
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(600, 100)) + slideInVertically(tween(600, 100)) { -50 },
            ) {
                ArchetypeCard(
                    name = wrap.archetype.name,
                    tagline = wrap.archetype.tagline,
                    bullets = wrap.archetype.bullets,
                )
            }
        }

        item {
            AnimatedVisibility(
                visible = visible,
                enter = fadeIn(tween(700, 200)) + slideInVertically(tween(700, 200)) { -50 },
            ) {
                WrapStatsRow(wrap)
            }
        }

        wrap.sectionCopy.genresLine?.let { genresLine ->
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, 300)) + slideInVertically(tween(800, 300)) { -50 },
                ) {
                    WrapSectionCard(
                        icon = Icons.Default.Movie,
                        title = stringResource(R.string.wrap_genres_title),
                        subtitle = genresLine,
                        content = {
                            GenreChips(wrap.stats.topGenres.map { it.name })
                        },
                    )
                }
            }
        }

        if (wrap.stats.topMovies.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(900, 400)) + slideInVertically(tween(900, 400)) { -50 },
                ) {
                    WrapSectionCard(
                        icon = Icons.Default.Star,
                        title = stringResource(R.string.wrap_top_movies_title),
                        subtitle = wrap.sectionCopy.moviesLine,
                        content = {
                            TopMoviesRow(wrap.stats.topMovies.take(5))
                        },
                    )
                }
            }
        }

        if (wrap.stats.topActors.isNotEmpty()) {
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(1000, 500)) + slideInVertically(tween(1000, 500)) { -50 },
                ) {
                    WrapSectionCard(
                        icon = Icons.Default.Person,
                        title = stringResource(R.string.wrap_actor_title),
                        subtitle = wrap.sectionCopy.actorLine,
                        content = {
                            PersonRow(wrap.stats.topActors.take(3))
                        },
                    )
                }
            }
        }

        wrap.sectionCopy.aiLine?.let { aiLine ->
            item {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(1100, 600)) + slideInVertically(tween(1100, 600)) { -50 },
                ) {
                    WrapSectionCard(
                        icon = Icons.Default.AutoAwesome,
                        title = stringResource(R.string.wrap_ai_search_title),
                        subtitle = aiLine,
                        content = {
                            wrap.stats.aiSearchHighlights?.let { highlights ->
                                AiSearchHighlightsContent(highlights)
                            }
                        },
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(8.dp))
            WrapShareButton(onClick = onShareClick)
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun EmptyContent(
    interactionsNeeded: Int,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "🎬",
            fontSize = 64.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.wrap_empty_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = stringResource(R.string.wrap_empty_subtitle, interactionsNeeded),
            style = MaterialTheme.typography.bodyLarge,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(32.dp))

        Card(
            colors = CardDefaults.cardColors(
                containerColor = GalaxyPurple40.copy(alpha = 0.4f),
            ),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
            ) {
                Text(
                    text = stringResource(R.string.wrap_empty_suggestions_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = StarBright,
                )
                Spacer(modifier = Modifier.height(12.dp))
                WrapSuggestionItem("🎥", stringResource(R.string.wrap_empty_suggestion_1))
                WrapSuggestionItem("⭐", stringResource(R.string.wrap_empty_suggestion_2))
                WrapSuggestionItem("🔍", stringResource(R.string.wrap_empty_suggestion_3))
            }
        }
    }
}

@Composable
private fun ErrorContent(
    message: String,
    onRetry: () -> Unit,
    padding: PaddingValues,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(padding)
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = "😕",
            fontSize = 64.sp,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = stringResource(R.string.wrap_error_title),
            style = MaterialTheme.typography.headlineSmall,
            color = Color.White,
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(12.dp))
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = Color.White.copy(alpha = 0.7f),
            textAlign = TextAlign.Center,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(
            onClick = onRetry,
            colors = ButtonDefaults.buttonColors(
                containerColor = CinemaOrange,
            ),
        ) {
            Text(text = stringResource(R.string.retry))
        }
    }
}
