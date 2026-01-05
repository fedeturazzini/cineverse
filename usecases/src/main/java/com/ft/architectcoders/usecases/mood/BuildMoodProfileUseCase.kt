package com.ft.architectcoders.usecases.mood

import com.ft.architectcoders.data.repository.mood.MoodRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector

interface BuildMoodProfileUseCase {
    suspend operator fun invoke(moodVector: MoodVector): Result<MoodProfile>
}

class BuildMoodProfileUseCaseImpl(
    private val moodRepository: MoodRepository,
) : BuildMoodProfileUseCase {
    override suspend fun invoke(moodVector: MoodVector): Result<MoodProfile> {
        return moodRepository.buildMoodProfile(moodVector)
    }
}
