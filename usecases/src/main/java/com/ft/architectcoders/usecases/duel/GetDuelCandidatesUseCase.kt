package com.ft.architectcoders.usecases.duel

import com.ft.architectcoders.data.repository.duel.DuelRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.Movie

interface GetDuelCandidatesUseCase {
    suspend operator fun invoke(): Result<List<Movie>>
}

class GetDuelCandidatesUseCaseImpl(
    private val duelRepository: DuelRepository,
) : GetDuelCandidatesUseCase {
    override suspend fun invoke(): Result<List<Movie>> {
        return duelRepository.getDuelCandidates(CANDIDATES_COUNT)
    }

    companion object {
        private const val CANDIDATES_COUNT = 20
    }
}
