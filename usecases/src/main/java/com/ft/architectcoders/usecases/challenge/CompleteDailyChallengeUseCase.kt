package com.ft.architectcoders.usecases.challenge

import com.ft.architectcoders.data.repository.challenge.ChallengeRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DailyChallenge

interface CompleteDailyChallengeUseCase {
    suspend operator fun invoke(
        date: String,
        movieId: Int,
    ): Result<DailyChallenge>
}

class CompleteDailyChallengeUseCaseImpl(
    private val challengeRepository: ChallengeRepository,
) : CompleteDailyChallengeUseCase {
    override suspend fun invoke(
        date: String,
        movieId: Int,
    ): Result<DailyChallenge> {
        return challengeRepository.completeChallenge(date, movieId)
    }
}

