package com.ft.architectcoders.usecases.marathon

import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.domain.model.MarathonPlan

interface GetMarathonByIdUseCase {
    suspend operator fun invoke(marathonId: Long): MarathonPlan?
}

class GetMarathonByIdUseCaseImpl(
    private val marathonRepository: MarathonRepository,
) : GetMarathonByIdUseCase {
    override suspend fun invoke(marathonId: Long): MarathonPlan? {
        return marathonRepository.getMarathonById(marathonId)
    }
}

