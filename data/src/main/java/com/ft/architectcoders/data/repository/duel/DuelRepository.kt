package com.ft.architectcoders.data.repository.duel

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.Movie

interface DuelRepository {
    suspend fun getDuelCandidates(count: Int): Result<List<Movie>>
    suspend fun saveDuelSession(session: DuelSession): Long
    suspend fun markSessionCompleted(sessionId: Long)
}

