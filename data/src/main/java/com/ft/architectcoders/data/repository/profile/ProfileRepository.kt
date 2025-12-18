package com.ft.architectcoders.data.repository.profile

import com.ft.architectcoders.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    val profile: Flow<Profile>

    suspend fun saveProfile(profile: Profile)
}
