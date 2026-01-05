package com.ft.architectcoders.usecases.marathon

import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.domain.model.MarathonTheme

interface GetMarathonThemesUseCase {
    operator fun invoke(): List<MarathonTheme>
}

class GetMarathonThemesUseCaseImpl(
    private val marathonRepository: MarathonRepository,
) : GetMarathonThemesUseCase {
    override fun invoke(): List<MarathonTheme> {
        return marathonRepository.getThemes()
    }
}

