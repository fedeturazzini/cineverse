package com.ft.architectcoders.domain.model

enum class MarathonThemeId {
    LAUGH,
    CRY,
    SHORT,
    RAINY_DAY,
    SUNDAY_MODE,
    ADRENALINE,
    ROMANCE,
    PLOT_TWIST,
    CEREBRAL,
    FRIENDS_NIGHT,
    DISCOVER_NEW,
}

data class MarathonTheme(
    val id: MarathonThemeId,
    val title: String,
    val emoji: String,
    val targetItems: Int = 3,
    val maxTotalMinutes: Int = 360,
)

data class MarathonPick(
    val movieId: Int,
    val movieTitle: String,
    val moviePoster: String?,
    val movieYear: String?,
    val movieRating: Double?,
    val order: Int,
    val why: String,
    val warnings: List<String> = emptyList(),
)

data class MarathonPlan(
    val id: Long = 0,
    val themeId: MarathonThemeId,
    val themeTitle: String,
    val tagline: String,
    val picks: List<MarathonPick>,
    val generatedByAi: Boolean = true,
    val timestamp: Long = System.currentTimeMillis(),
)

data class MarathonHistoryItem(
    val id: Long,
    val themeId: MarathonThemeId,
    val themeTitle: String,
    val timestamp: Long,
    val movieCount: Int,
)

data class MarathonCandidate(
    val id: Int,
    val title: String,
    val overview: String,
    val genreIds: List<Int>,
    val voteAverage: Double,
    val popularity: Double,
    val year: String?,
    val poster: String?,
)

