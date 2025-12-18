package com.ft.architectcoders.data.repository.profile

import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.data.datasource.ProfileLocalDataSourceImpl
import com.ft.architectcoders.domain.model.UserProfile
import kotlinx.coroutines.flow.Flow

class ProfileRepositoryImpl(
    private val localDataSource: ProfileLocalDataSource
    ,
) : ProfileRepository {
    override val profile: Flow<UserProfile> = localDataSource.profile

    override suspend fun saveProfile(profile: UserProfile) {
        localDataSource.saveProfile(profile)
    }
}
