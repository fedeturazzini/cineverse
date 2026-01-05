package com.ft.architectcoders.usecases.duel

import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.DuelSession

interface SubmitDuelChoiceUseCase {
    operator fun invoke(
        session: DuelSession,
        choice: DuelChoice,
    ): DuelSession
}

class SubmitDuelChoiceUseCaseImpl : SubmitDuelChoiceUseCase {
    override fun invoke(
        session: DuelSession,
        choice: DuelChoice,
    ): DuelSession {
        return session.copy(
            choices = session.choices + choice,
        )
    }
}
