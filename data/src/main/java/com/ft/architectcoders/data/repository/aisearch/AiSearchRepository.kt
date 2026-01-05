package com.ft.architectcoders.data.repository.aisearch

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchTurnResult
import com.ft.architectcoders.domain.model.AiSearchUserSignals

interface AiSearchRepository {
    suspend fun processUserMessage(
        session: AiSearchSession,
        userMessage: String,
        userSignals: AiSearchUserSignals,
    ): Result<AiSearchTurnResult>

    suspend fun saveSession(session: AiSearchSession): Long
    suspend fun getSessionById(id: Long): AiSearchSession?
}

