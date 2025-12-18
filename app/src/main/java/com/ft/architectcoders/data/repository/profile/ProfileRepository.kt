package com.ft.architectcoders.data.repository.profile

import com.ft.architectcoders.domain.model.Profile
import com.ft.architectcoders.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profile: Flow<UserProfile>

    suspend fun saveProfile(profile: UserProfile)
}
