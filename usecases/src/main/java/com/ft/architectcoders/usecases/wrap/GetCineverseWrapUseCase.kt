package com.ft.architectcoders.usecases.wrap

import com.ft.architectcoders.data.repository.wrap.WrapRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.domain.model.WrapGeminiInput

interface GetCineverseWrapUseCase {
    suspend operator fun invoke(): Result<CineverseWrapResult>
}

sealed interface CineverseWrapResult {
    data class Success(val wrap: CineverseWrap) : CineverseWrapResult
    data class Empty(val interactionsNeeded: Int) : CineverseWrapResult
}

class GetCineverseWrapUseCaseImpl(
    private val buildWrapStatsUseCase: BuildWrapStatsUseCase,
    private val wrapRepository: WrapRepository,
) : GetCineverseWrapUseCase {

    companion object {
        private const val MIN_INTERACTIONS_REQUIRED = 3
    }

    override suspend fun invoke(): Result<CineverseWrapResult> {
        val statsResult = buildWrapStatsUseCase()

        if (statsResult.isEmpty || statsResult.stats == null) {
            val currentInteractions = 0
            val needed = MIN_INTERACTIONS_REQUIRED - currentInteractions
            return Result.Success(CineverseWrapResult.Empty(interactionsNeeded = needed))
        }

        val stats = statsResult.stats
        val totalInteractions = stats.totals.moviesViewed + stats.totals.favorites + stats.totals.aiSearchSessions

        if (totalInteractions < MIN_INTERACTIONS_REQUIRED) {
            val needed = MIN_INTERACTIONS_REQUIRED - totalInteractions
            return Result.Success(CineverseWrapResult.Empty(interactionsNeeded = needed))
        }

        val geminiInput = WrapGeminiInput(
            period = stats.period,
            profileName = statsResult.profile.name.takeIf { it.isNotBlank() },
            profileRegion = statsResult.profile.region.takeIf { it.isNotBlank() },
            stats = stats,
        )

        return when (val wrapResult = wrapRepository.generateWrap(geminiInput)) {
            is Result.Success -> {
                Result.Success(CineverseWrapResult.Success(wrapResult.data))
            }
            is Result.Error -> {
                Result.Error(wrapResult.error)
            }
            is Result.Loading -> {
                Result.Loading
            }
        }
    }
}
