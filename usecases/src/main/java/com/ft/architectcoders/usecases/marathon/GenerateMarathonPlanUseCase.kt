package com.ft.architectcoders.usecases.marathon

import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonThemeId

interface GenerateMarathonPlanUseCase {
    suspend operator fun invoke(themeId: MarathonThemeId): Result<MarathonPlan>
}

class GenerateMarathonPlanUseCaseImpl(
    private val marathonRepository: MarathonRepository,
) : GenerateMarathonPlanUseCase {
    override suspend fun invoke(themeId: MarathonThemeId): Result<MarathonPlan> {
        return marathonRepository.generatePlan(themeId)
    }
}

