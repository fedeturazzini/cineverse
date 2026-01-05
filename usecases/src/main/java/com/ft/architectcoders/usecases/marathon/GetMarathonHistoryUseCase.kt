package com.ft.architectcoders.usecases.marathon

import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.domain.model.MarathonHistoryItem
import kotlinx.coroutines.flow.Flow

interface GetMarathonHistoryUseCase {
    operator fun invoke(): Flow<List<MarathonHistoryItem>>
}

class GetMarathonHistoryUseCaseImpl(
    private val marathonRepository: MarathonRepository,
) : GetMarathonHistoryUseCase {
    override fun invoke(): Flow<List<MarathonHistoryItem>> {
        return marathonRepository.getHistory()
    }
}

