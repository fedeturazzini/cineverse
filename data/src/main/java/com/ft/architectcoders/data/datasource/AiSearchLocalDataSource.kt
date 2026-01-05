package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.AiSearchSession
import kotlinx.coroutines.flow.Flow

interface AiSearchLocalDataSource {
    suspend fun saveSession(session: AiSearchSession): Long
    suspend fun getSessionById(id: Long): AiSearchSession?
    fun getAllSessions(): Flow<List<AiSearchSession>>
}

