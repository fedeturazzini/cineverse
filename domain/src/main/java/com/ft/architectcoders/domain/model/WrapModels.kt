package com.ft.architectcoders.domain.model

data class WrapPeriod(
    val fromDate: String,
    val toDate: String,
)

data class WrapGenreCount(
    val id: Int,
    val name: String,
    val count: Int,
)

data class WrapMovieScore(
    val id: Int,
    val title: String,
    val year: Int,
    val poster: String,
    val score: Int,
)

data class WrapPersonCount(
    val id: Int,
    val name: String,
    val profilePhoto: String?,
    val count: Int,
)

data class WrapAiSearchHighlights(
    val topIntents: List<String>,
    val topKeywords: List<String>,
    val sessionCount: Int,
)

data class WrapStats(
    val period: WrapPeriod,
    val totals: WrapTotals,
    val topGenres: List<WrapGenreCount>,
    val topMovies: List<WrapMovieScore>,
    val favoritesHighlights: List<WrapMovieScore>,
    val topActors: List<WrapPersonCount>,
    val topDirectors: List<WrapPersonCount>,
    val aiSearchHighlights: WrapAiSearchHighlights?,
)


data class WrapTotals(
    val moviesViewed: Int,
    val favorites: Int,
    val aiSearchSessions: Int,
)


data class WrapArchetype(
    val name: String,
    val tagline: String,
    val bullets: List<String>,
)


data class WrapSectionCopy(
    val genresLine: String?,
    val moviesLine: String?,
    val aiLine: String?,
    val actorLine: String?,
    val directorLine: String?,
)


data class CineverseWrap(
    val period: WrapPeriod,
    val funnyProfileSummary: String,
    val archetype: WrapArchetype,
    val sectionCopy: WrapSectionCopy,
    val shareText: String,
    val stats: WrapStats,
    val generatedByAi: Boolean,
)

data class WrapGeminiInput(
    val period: WrapPeriod,
    val profileName: String?,
    val profileRegion: String?,
    val stats: WrapStats,
)

