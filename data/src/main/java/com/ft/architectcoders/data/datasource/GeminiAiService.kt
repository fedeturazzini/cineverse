package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.MarathonCandidate
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.TasteFingerprint

data class MarathonAiResult(
    val tagline: String,
    val picks: List<MarathonAiPick>,
)

data class MarathonAiPick(
    val movieId: Int,
    val order: Int,
    val why: String,
    val warnings: List<String> = emptyList(),
)

interface GeminiAiService {
    suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview>

    suspend fun generateTasteFingerprint(
        sessionId: Long,
        choices: List<DuelChoice>,
    ): Result<TasteFingerprint>

    suspend fun generateMoodProfile(moodVector: MoodVector): Result<MoodProfile>

    suspend fun generateMarathonPlan(
        theme: MarathonTheme,
        candidates: List<MarathonCandidate>,
        topGenres: List<Int>,
        region: String,
    ): Result<MarathonAiResult>

    suspend fun generateDailyChallenge(
        date: String,
        topGenres: List<Pair<Int, Int>>,
        decadeHistogram: List<Pair<Int, Int>>,
        favoriteMovieIds: List<Int>,
    ): Result<ChallengeAiResult>
}
