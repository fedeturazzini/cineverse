package com.ft.architectcoders.test

import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchTurnResult
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.ChatRole
import com.ft.architectcoders.domain.model.DetectedPreferences
import com.ft.architectcoders.domain.model.Movie

fun sampleChatMessage(
    role: ChatRole = ChatRole.ASSISTANT,
    content: String = "Test message",
    movieResults: List<Movie> = emptyList(),
) = ChatMessage(
    role = role,
    content = content,
    timestamp = System.currentTimeMillis(),
    movieResults = movieResults,
)

fun sampleAiSearchSession(
    id: Long = 0,
    messages: List<ChatMessage> = listOf(sampleChatMessage()),
    isCompleted: Boolean = false,
) = AiSearchSession(
    id = id,
    createdAt = System.currentTimeMillis(),
    messages = messages,
    detectedPreferences = null,
    recommendedMovieIds = emptyList(),
    finalBullets = emptyList(),
    isCompleted = isCompleted,
)

fun sampleAiSearchTurnResult(
    assistantMessage: String = "Test response",
    movies: List<Movie> = sampleMovies(1, 2, 3),
    isOutOfScope: Boolean = false,
    finalBullets: List<String>? = null,
) = AiSearchTurnResult(
    assistantMessage = assistantMessage,
    movies = movies,
    detectedPreferences = sampleDetectedPreferences(),
    isOutOfScope = isOutOfScope,
    finalBullets = finalBullets,
)

fun sampleDetectedPreferences() = DetectedPreferences(
    includeGenreIds = listOf(28, 878),
    excludeGenreIds = emptyList(),
    yearFrom = 2000,
    yearTo = 2025,
    maxRuntimeMinutes = 150,
    minVoteAverage = 7.0f,
    keywords = listOf("action", "sci-fi"),
    similarToTitles = emptyList(),
)

