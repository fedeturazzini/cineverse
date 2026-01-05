package com.ft.architectcoders.usecases.aisearch

import com.ft.architectcoders.data.repository.aisearch.AiSearchRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchTurnResult
import com.ft.architectcoders.domain.model.AiSearchUserSignals

interface SendAiSearchMessageUseCase {
    suspend operator fun invoke(
        session: AiSearchSession,
        userMessage: String,
        userSignals: AiSearchUserSignals,
    ): Result<AiSearchTurnResult>
}

class SendAiSearchMessageUseCaseImpl(
    private val aiSearchRepository: AiSearchRepository,
) : SendAiSearchMessageUseCase {
    override suspend fun invoke(
        session: AiSearchSession,
        userMessage: String,
        userSignals: AiSearchUserSignals,
    ): Result<AiSearchTurnResult> {
        return aiSearchRepository.processUserMessage(session, userMessage, userSignals)
    }
}

