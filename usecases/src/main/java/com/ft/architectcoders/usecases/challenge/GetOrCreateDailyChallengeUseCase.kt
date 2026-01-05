package com.ft.architectcoders.usecases.challenge

import com.ft.architectcoders.data.repository.challenge.ChallengeRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.UserChallengeSignals

interface GetOrCreateDailyChallengeUseCase {
    suspend operator fun invoke(
        date: String,
        signals: UserChallengeSignals,
    ): Result<DailyChallenge>
}

class GetOrCreateDailyChallengeUseCaseImpl(
    private val challengeRepository: ChallengeRepository,
) : GetOrCreateDailyChallengeUseCase {
    override suspend fun invoke(
        date: String,
        signals: UserChallengeSignals,
    ): Result<DailyChallenge> {
        return challengeRepository.getOrCreateDailyChallenge(date, signals)
    }
}

