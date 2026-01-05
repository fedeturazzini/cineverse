package com.ft.architectcoders.domain.model

data class MoodVector(
    val energy: Int = 50,
    val humor: Int = 50,
    val tension: Int = 50,
    val romance: Int = 50,
    val cerebral: Int = 50,
)

data class GenreWeight(
    val id: Int,
    val weight: Float,
)

data class MoodProfile(
    val microCopy: String,
    val genres: List<GenreWeight>,
    val excludeGenres: List<Int> = emptyList(),
    val globalExplanation: List<String>,
    val sortBy: String = "popularity.desc",
    val minVoteAverage: Float? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val generatedByAi: Boolean,
)

data class MoodRecommendation(
    val profile: MoodProfile,
    val movies: List<Movie>,
)
