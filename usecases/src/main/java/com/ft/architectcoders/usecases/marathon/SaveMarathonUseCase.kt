package com.ft.architectcoders.usecases.marathon

import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.domain.model.MarathonPlan

interface SaveMarathonUseCase {
    suspend operator fun invoke(plan: MarathonPlan): Long
}

class SaveMarathonUseCaseImpl(
    private val marathonRepository: MarathonRepository,
) : SaveMarathonUseCase {
    override suspend fun invoke(plan: MarathonPlan): Long {
        return marathonRepository.savePlan(plan)
    }
}

