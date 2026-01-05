package com.ft.architectcoders.data.repository.taste

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint
import kotlinx.coroutines.flow.Flow

interface TasteRepository {
    suspend fun generateFingerprint(session: DuelSession): Result<TasteFingerprint>

    fun getLastFingerprint(): Flow<TasteFingerprint?>
}
