package com.ft.architectcoders.ui.screens.wrap.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ft.architectcoders.R
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.ui.theme.CinemaOrange
import com.ft.architectcoders.ui.theme.GalaxyPurple80
import com.ft.architectcoders.ui.theme.StarBright

@Composable
fun WrapStatsRow(
    wrap: CineverseWrap,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        StatCard(
            modifier = Modifier.weight(1f),
            value = wrap.stats.totals.moviesViewed.toString(),
            label = stringResource(R.string.wrap_stat_movies),
            color = StarBright,
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = wrap.stats.totals.favorites.toString(),
            label = stringResource(R.string.wrap_stat_favorites),
            color = CinemaOrange,
        )
        StatCard(
            modifier = Modifier.weight(1f),
            value = wrap.stats.totals.aiSearchSessions.toString(),
            label = stringResource(R.string.wrap_stat_searches),
            color = GalaxyPurple80,
        )
    }
}

@Composable
private fun StatCard(
    value: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = color.copy(alpha = 0.2f),
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                color = color,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.7f),
            )
        }
    }
}

