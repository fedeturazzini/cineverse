package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.Profile
import kotlinx.coroutines.flow.Flow

interface ProfileLocalDataSource {
    val profile: Flow<Profile>

    suspend fun saveProfile(profile: Profile)
}
