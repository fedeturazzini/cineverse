package com.ft.architectcoders.test

import com.ft.architectcoders.domain.model.GenreWeight
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodRecommendation
import com.ft.architectcoders.domain.model.MoodVector

fun sampleMoodVector(
    energy: Int = 50,
    humor: Int = 50,
    tension: Int = 50,
    romance: Int = 50,
    cerebral: Int = 50,
) = MoodVector(
    energy = energy,
    humor = humor,
    tension = tension,
    romance = romance,
    cerebral = cerebral,
)

fun sampleMoodProfile(
    microCopy: String = "Hoy estás para comedias ligeras con algo de acción",
    genres: List<GenreWeight> = listOf(
        GenreWeight(35, 0.9f), // Comedy
        GenreWeight(28, 0.6f), // Action
    ),
    excludeGenres: List<Int> = listOf(27), // Horror
    globalExplanation: List<String> = listOf(
        "Tu humor está alto, necesitas reír",
        "Algo de energía para mantenerte despierto",
        "Nada muy oscuro esta noche",
    ),
    sortBy: String = "popularity.desc",
    minVoteAverage: Float? = 6.5f,
    yearFrom: Int? = 2000,
    yearTo: Int? = 2025,
    generatedByAi: Boolean = true,
) = MoodProfile(
    microCopy = microCopy,
    genres = genres,
    excludeGenres = excludeGenres,
    globalExplanation = globalExplanation,
    sortBy = sortBy,
    minVoteAverage = minVoteAverage,
    yearFrom = yearFrom,
    yearTo = yearTo,
    generatedByAi = generatedByAi,
)

fun sampleMoodRecommendation(
    profile: MoodProfile = sampleMoodProfile(),
    movieCount: Int = 10,
) = MoodRecommendation(
    profile = profile,
    movies = sampleMovies(*(1..movieCount).toList().toIntArray()),
)

