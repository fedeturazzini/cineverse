package com.ft.architectcoders.usecases.challenge

import com.ft.architectcoders.data.repository.challenge.ChallengeRepository
import com.ft.architectcoders.domain.model.DailyChallenge
import kotlinx.coroutines.flow.Flow

interface GetChallengeHistoryUseCase {
    operator fun invoke(): Flow<List<DailyChallenge>>
}

class GetChallengeHistoryUseCaseImpl(
    private val challengeRepository: ChallengeRepository,
) : GetChallengeHistoryUseCase {
    override fun invoke(): Flow<List<DailyChallenge>> {
        return challengeRepository.getChallengeHistory()
    }
}

