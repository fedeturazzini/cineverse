package com.ft.architectcoders.usecases.aisearch

import com.ft.architectcoders.data.repository.aisearch.AiSearchRepository
import com.ft.architectcoders.domain.model.AiSearchSession

interface GetAiSearchSessionUseCase {
    suspend operator fun invoke(id: Long): AiSearchSession?
}

class GetAiSearchSessionUseCaseImpl(
    private val aiSearchRepository: AiSearchRepository,
) : GetAiSearchSessionUseCase {
    override suspend fun invoke(id: Long): AiSearchSession? {
        return aiSearchRepository.getSessionById(id)
    }
}

