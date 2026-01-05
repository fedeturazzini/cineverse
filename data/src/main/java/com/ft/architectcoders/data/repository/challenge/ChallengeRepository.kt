package com.ft.architectcoders.data.repository.challenge

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.UserChallengeSignals
import kotlinx.coroutines.flow.Flow

interface ChallengeRepository {
    suspend fun getOrCreateDailyChallenge(
        date: String,
        signals: UserChallengeSignals,
    ): Result<DailyChallenge>

    suspend fun completeChallenge(
        date: String,
        movieId: Int,
    ): Result<DailyChallenge>

    fun getChallengeHistory(): Flow<List<DailyChallenge>>

    fun getUnlockedBadges(): Flow<List<ChallengeBadge>>
}

