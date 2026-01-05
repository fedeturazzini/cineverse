package com.ft.architectcoders.test

import com.ft.architectcoders.domain.model.MarathonHistoryItem
import com.ft.architectcoders.domain.model.MarathonPick
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MarathonThemeId

fun sampleMarathonTheme(
    id: MarathonThemeId = MarathonThemeId.LAUGH,
    title: String = "Quiero reírme",
    emoji: String = "😂",
    targetItems: Int = 3,
    maxTotalMinutes: Int = 360,
) = MarathonTheme(id, title, emoji, targetItems, maxTotalMinutes)

fun sampleMarathonPick(
    movieId: Int = 1,
    movieTitle: String = "Funny Movie",
    moviePoster: String? = "https://image.tmdb.org/t/p/w500/poster.jpg",
    movieYear: String? = "2023",
    movieRating: Double? = 7.5,
    order: Int = 1,
    why: String = "Perfect for laughing",
    warnings: List<String> = emptyList(),
) = MarathonPick(
    movieId = movieId,
    movieTitle = movieTitle,
    moviePoster = moviePoster,
    movieYear = movieYear,
    movieRating = movieRating,
    order = order,
    why = why,
    warnings = warnings,
)

fun sampleMarathonPlan(
    id: Long = 1,
    themeId: MarathonThemeId = MarathonThemeId.LAUGH,
    themeTitle: String = "Quiero reírme",
    tagline: String = "Una noche de risas garantizadas",
    picks: List<MarathonPick> = listOf(
        sampleMarathonPick(movieId = 1, order = 1),
        sampleMarathonPick(movieId = 2, order = 2, movieTitle = "Comedy 2"),
        sampleMarathonPick(movieId = 3, order = 3, movieTitle = "Comedy 3"),
    ),
    generatedByAi: Boolean = true,
    timestamp: Long = System.currentTimeMillis(),
) = MarathonPlan(
    id = id,
    themeId = themeId,
    themeTitle = themeTitle,
    tagline = tagline,
    picks = picks,
    generatedByAi = generatedByAi,
    timestamp = timestamp,
)

fun sampleMarathonHistoryItem(
    id: Long = 1,
    themeId: MarathonThemeId = MarathonThemeId.LAUGH,
    themeTitle: String = "Quiero reírme",
    timestamp: Long = System.currentTimeMillis(),
    movieCount: Int = 3,
) = MarathonHistoryItem(
    id = id,
    themeId = themeId,
    themeTitle = themeTitle,
    timestamp = timestamp,
    movieCount = movieCount,
)

