package com.ft.architectcoders.usecases.challenge

import com.ft.architectcoders.data.repository.challenge.ChallengeRepository
import com.ft.architectcoders.domain.model.ChallengeBadge
import kotlinx.coroutines.flow.Flow

interface GetUnlockedBadgesUseCase {
    operator fun invoke(): Flow<List<ChallengeBadge>>
}

class GetUnlockedBadgesUseCaseImpl(
    private val challengeRepository: ChallengeRepository,
) : GetUnlockedBadgesUseCase {
    override fun invoke(): Flow<List<ChallengeBadge>> {
        return challengeRepository.getUnlockedBadges()
    }
}

