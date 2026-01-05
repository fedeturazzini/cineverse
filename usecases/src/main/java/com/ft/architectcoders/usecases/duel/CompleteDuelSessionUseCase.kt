package com.ft.architectcoders.usecases.duel

import com.ft.architectcoders.data.repository.duel.DuelRepository
import com.ft.architectcoders.data.repository.taste.TasteRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint

interface CompleteDuelSessionUseCase {
    suspend operator fun invoke(session: DuelSession): Result<TasteFingerprint>
}

class CompleteDuelSessionUseCaseImpl(
    private val duelRepository: DuelRepository,
    private val tasteRepository: TasteRepository,
) : CompleteDuelSessionUseCase {
    override suspend fun invoke(session: DuelSession): Result<TasteFingerprint> {
        val completedSession = session.copy(isCompleted = true)

        val sessionId = duelRepository.saveDuelSession(completedSession)
        duelRepository.markSessionCompleted(sessionId)

        val sessionWithId = completedSession.copy(id = sessionId)

        return tasteRepository.generateFingerprint(sessionWithId)
    }
}
