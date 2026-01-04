package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint
import kotlinx.coroutines.flow.Flow

interface DuelLocalDataSource {
    suspend fun saveDuelSession(session: DuelSession): Long
    suspend fun saveTasteFingerprint(fingerprint: TasteFingerprint)
    fun getLastFingerprint(): Flow<TasteFingerprint?>
    fun getDuelHistory(): Flow<List<DuelSession>>
    suspend fun markSessionCompleted(sessionId: Long)
}

