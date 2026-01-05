package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.DetectedPreferences
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.MarathonCandidate
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.TasteFingerprint
import com.ft.architectcoders.domain.model.WrapGeminiInput

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

data class AiSearchGeminiResponse(
    val assistantMessage: String,
    val isOutOfScope: Boolean,
    val detectedPreferences: DetectedPreferences?,
    val tmdbQueryPlan: TmdbQueryPlan?,
    val followupQuestion: String?,
    val finalBullets: List<String>?,
)

data class TmdbQueryPlan(
    val type: String,
    val searchQuery: String?,
    val genres: List<Int>?,
    val excludeGenres: List<Int>?,
    val minVoteAverage: Float?,
    val yearFrom: Int?,
    val yearTo: Int?,
    val sortBy: String?,
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

    suspend fun generateAiSearchResponse(
        conversationHistory: List<ChatMessage>,
        turnIndex: Int,
        region: String,
        topGenreIds: List<Int>,
        avoidMovieIds: List<Int>,
    ): Result<AiSearchGeminiResponse>

    suspend fun generateCineverseWrap(input: WrapGeminiInput): Result<WrapAiResult>
}
