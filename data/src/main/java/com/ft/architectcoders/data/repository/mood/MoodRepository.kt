package com.ft.architectcoders.data.repository.mood

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.Movie

interface MoodRepository {
    suspend fun buildMoodProfile(moodVector: MoodVector): Result<MoodProfile>
    suspend fun getRecommendations(profile: MoodProfile): Result<List<Movie>>
}
