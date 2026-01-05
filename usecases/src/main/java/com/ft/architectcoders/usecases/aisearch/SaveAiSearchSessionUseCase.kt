package com.ft.architectcoders.usecases.aisearch

import com.ft.architectcoders.data.repository.aisearch.AiSearchRepository
import com.ft.architectcoders.domain.model.AiSearchSession

interface SaveAiSearchSessionUseCase {
    suspend operator fun invoke(session: AiSearchSession): Long
}

class SaveAiSearchSessionUseCaseImpl(
    private val aiSearchRepository: AiSearchRepository,
) : SaveAiSearchSessionUseCase {
    override suspend fun invoke(session: AiSearchSession): Long {
        return aiSearchRepository.saveSession(session)
    }
}

