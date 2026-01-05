package com.ft.architectcoders.domain.model

enum class ChallengeType {
    NEW_GENRE,
    PRE_2000,
    NO_SEQUELS_WEEK,
    CLASSIC_AWARD_WINNER,
}

enum class ChallengeStatus {
    ACTIVE,
    COMPLETED,
    FAILED,
}

data class ChallengeBadge(
    val id: String,
    val name: String,
    val emoji: String,
    val unlockedAt: Long? = null,
)

data class ChallengeMovieOption(
    val movieId: Int,
    val title: String,
    val poster: String?,
    val year: String?,
    val whyItFits: String,
    val isCorrectChoice: Boolean = true,
)

data class DailyChallenge(
    val date: String, // YYYY-MM-DD
    val type: ChallengeType,
    val title: String,
    val reason: String,
    val rules: List<String>,
    val badge: ChallengeBadge,
    val movieOptions: List<ChallengeMovieOption>,
    val status: ChallengeStatus,
    val completedMovieId: Int? = null,
    val completedAt: Long? = null,
    val generatedByAi: Boolean = true,
)

data class UserChallengeSignals(
    val topGenres: List<GenreCount>,
    val decadeHistogram: List<DecadeCount>,
    val favoriteMovieIds: List<Int>,
)

data class GenreCount(
    val id: Int,
    val name: String,
    val count: Int,
)

data class DecadeCount(
    val decade: Int,
    val count: Int,
)

