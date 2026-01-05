package com.ft.architectcoders.domain.model

data class ChatMessage(
    val role: ChatRole,
    val content: String,
    val timestamp: Long = System.currentTimeMillis(),
    val movieResults: List<Movie> = emptyList(),
)

enum class ChatRole { USER, ASSISTANT }

data class AiSearchSession(
    val id: Long = 0,
    val createdAt: Long,
    val messages: List<ChatMessage>,
    val detectedPreferences: DetectedPreferences?,
    val recommendedMovieIds: List<Int>,
    val finalBullets: List<String>,
    val isCompleted: Boolean,
)

data class DetectedPreferences(
    val includeGenreIds: List<Int>,
    val excludeGenreIds: List<Int>,
    val yearFrom: Int?,
    val yearTo: Int?,
    val maxRuntimeMinutes: Int?,
    val minVoteAverage: Float?,
    val keywords: List<String>,
    val similarToTitles: List<String>,
)

data class AiSearchUserSignals(
    val region: String,
    val topGenreIds: List<Int>,
    val avoidMovieIds: List<Int>,
)

data class AiSearchTurnResult(
    val assistantMessage: String,
    val movies: List<Movie>,
    val detectedPreferences: DetectedPreferences?,
    val isOutOfScope: Boolean,
    val finalBullets: List<String>?,
)

