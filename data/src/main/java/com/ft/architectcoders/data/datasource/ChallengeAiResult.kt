package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.ChallengeType

data class ChallengeAiResult(
    val type: ChallengeType,
    val title: String,
    val reason: String,
    val rules: List<String>,
    val badgeId: String,
    val badgeName: String,
    val badgeEmoji: String,
    val filters: ChallengeFilters,
    val cardSubtitle: String,
    val completionCopy: String,
)

data class ChallengeFilters(
    val includeGenreIds: List<Int>? = null,
    val excludeGenreIds: List<Int>? = null,
    val yearFrom: Int? = null,
    val yearTo: Int? = null,
    val minVoteAverage: Float? = null,
    val excludeMovieIds: List<Int>? = null,
)

