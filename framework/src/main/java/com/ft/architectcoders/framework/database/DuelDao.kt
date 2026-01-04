package com.ft.architectcoders.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface DuelDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DbDuelSession): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChoices(choices: List<DbDuelChoice>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFingerprint(fingerprint: DbTasteFingerprint)

    @Query("SELECT * FROM duel_sessions WHERE id = :sessionId")
    suspend fun getSessionById(sessionId: Long): DbDuelSession?

    @Query("SELECT * FROM duel_choices WHERE sessionId = :sessionId ORDER BY roundNumber")
    suspend fun getChoicesBySessionId(sessionId: Long): List<DbDuelChoice>

    @Query("SELECT * FROM duel_sessions ORDER BY timestamp DESC")
    fun getAllSessions(): Flow<List<DbDuelSession>>

    @Query("SELECT * FROM taste_fingerprints ORDER BY timestamp DESC LIMIT 1")
    fun getLastFingerprint(): Flow<DbTasteFingerprint?>

    @Query("SELECT * FROM taste_fingerprints WHERE sessionId = :sessionId")
    suspend fun getFingerprintBySessionId(sessionId: Long): DbTasteFingerprint?

    @Query("UPDATE duel_sessions SET isCompleted = 1 WHERE id = :sessionId")
    suspend fun markSessionCompleted(sessionId: Long)

    @Transaction
    suspend fun saveDuelSessionWithChoices(
        session: DbDuelSession,
        choices: List<DbDuelChoice>,
    ): Long {
        val sessionId = insertSession(session)
        val choicesWithSessionId = choices.map { it.copy(sessionId = sessionId) }
        insertChoices(choicesWithSessionId)
        return sessionId
    }
}

