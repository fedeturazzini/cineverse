package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.DailyChallenge
import kotlinx.coroutines.flow.Flow

interface ChallengeLocalDataSource {
    suspend fun getChallengeByDate(date: String): DailyChallenge?

    suspend fun saveChallenge(challenge: DailyChallenge)

    suspend fun completeChallenge(
        date: String,
        movieId: Int,
    )

    suspend fun failChallenge(
        date: String,
        movieId: Int,
    )

    fun getChallengeHistory(): Flow<List<DailyChallenge>>

    fun getUnlockedBadges(): Flow<List<ChallengeBadge>>
}

