package com.ft.architectcoders.ui.screens.wrap.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.ft.architectcoders.domain.model.WrapAiSearchHighlights
import com.ft.architectcoders.ui.theme.StarBright

@Composable
fun AiSearchHighlightsContent(
    highlights: WrapAiSearchHighlights,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        if (highlights.topKeywords.isNotEmpty()) {
            Text(
                text = "Keywords más usados:",
                style = MaterialTheme.typography.labelMedium,
                color = Color.White.copy(alpha = 0.7f),
            )
            Spacer(modifier = Modifier.height(4.dp))
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                items(highlights.topKeywords) { keyword ->
                    Box(
                        modifier = Modifier
                            .background(
                                color = StarBright.copy(alpha = 0.2f),
                                shape = RoundedCornerShape(12.dp),
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp),
                    ) {
                        Text(
                            text = keyword,
                            style = MaterialTheme.typography.bodySmall,
                            color = StarBright,
                        )
                    }
                }
            }
        }
    }
}

