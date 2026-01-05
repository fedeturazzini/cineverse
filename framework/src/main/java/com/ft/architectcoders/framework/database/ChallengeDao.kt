package com.ft.architectcoders.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ChallengeDao {
    @Query("SELECT * FROM daily_challenges WHERE date = :date")
    suspend fun getChallengeByDate(date: String): DbDailyChallenge?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveChallenge(challenge: DbDailyChallenge)

    @Query("UPDATE daily_challenges SET status = 'COMPLETED', completedMovieId = :movieId, completedAt = :timestamp WHERE date = :date")
    suspend fun completeChallenge(
        date: String,
        movieId: Int,
        timestamp: Long,
    )

    @Query("UPDATE daily_challenges SET status = 'FAILED', completedMovieId = :movieId, completedAt = :timestamp WHERE date = :date")
    suspend fun failChallenge(
        date: String,
        movieId: Int,
        timestamp: Long,
    )

    @Query("SELECT * FROM daily_challenges ORDER BY date DESC")
    fun getChallengeHistory(): Flow<List<DbDailyChallenge>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveBadge(badge: DbChallengeBadge)

    @Query("SELECT * FROM challenge_badges ORDER BY unlockedAt DESC")
    fun getUnlockedBadges(): Flow<List<DbChallengeBadge>>
}

