package com.ft.architectcoders.data.repository.profile

import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.domain.model.Profile
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val localDataSource: ProfileLocalDataSource,
) : ProfileRepository {
    override val profile: Flow<Profile> = localDataSource.profile

    override suspend fun saveProfile(profile: Profile) {
        localDataSource.saveProfile(profile)
    }
}
