package com.ft.architectcoders.ui.screens.wrap.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ft.architectcoders.R
import com.ft.architectcoders.ui.theme.GalaxyPurple80
import com.ft.architectcoders.ui.theme.StarBright

@Composable
fun FunnyProfileSummaryCard(
    summary: String,
    generatedByAi: Boolean,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GalaxyPurple80.copy(alpha = 0.4f),
        ),
        shape = RoundedCornerShape(20.dp),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "✨",
                fontSize = 32.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = summary,
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Medium,
                lineHeight = 24.sp,
            )
            if (generatedByAi) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = stringResource(R.string.generated_by_ai),
                    style = MaterialTheme.typography.labelSmall,
                    color = StarBright.copy(alpha = 0.7f),
                )
            }
        }
    }
}

