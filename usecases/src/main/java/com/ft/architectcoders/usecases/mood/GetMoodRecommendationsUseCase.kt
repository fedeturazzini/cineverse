package com.ft.architectcoders.usecases.mood

import com.ft.architectcoders.data.repository.mood.MoodRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodRecommendation

interface GetMoodRecommendationsUseCase {
    suspend operator fun invoke(profile: MoodProfile): Result<MoodRecommendation>
}

class GetMoodRecommendationsUseCaseImpl(
    private val moodRepository: MoodRepository,
) : GetMoodRecommendationsUseCase {
    override suspend fun invoke(profile: MoodProfile): Result<MoodRecommendation> {
        return when (val result = moodRepository.getRecommendations(profile)) {
            is Result.Success -> {
                val dedupedMovies = result.data
                    .distinctBy { it.id }
                    .take(20)

                Result.Success(
                    MoodRecommendation(
                        profile = profile,
                        movies = dedupedMovies,
                    )
                )
            }
            is Result.Error -> result
            is Result.Loading -> result
        }
    }
}
