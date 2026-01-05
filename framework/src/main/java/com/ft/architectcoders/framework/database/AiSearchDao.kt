package com.ft.architectcoders.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AiSearchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: DbAiSearchSession): Long

    @Update
    suspend fun updateSession(session: DbAiSearchSession)

    @Query("SELECT * FROM ai_search_sessions WHERE id = :id")
    suspend fun getSessionById(id: Long): DbAiSearchSession?

    @Query("SELECT * FROM ai_search_sessions ORDER BY createdAt DESC")
    fun getAllSessions(): Flow<List<DbAiSearchSession>>
}

