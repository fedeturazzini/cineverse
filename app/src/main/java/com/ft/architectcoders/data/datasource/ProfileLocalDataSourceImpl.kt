package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.UserProfile
import com.ft.architectcoders.domain.model.toDomain
import com.ft.architectcoders.domain.model.toEntity
import com.ft.architectcoders.framework.database.ProfileDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

interface ProfileLocalDataSource {
    val profile: Flow<UserProfile>
    suspend fun saveProfile(profile: UserProfile)
}

class ProfileLocalDataSourceImpl(private val profileDao: ProfileDao): ProfileLocalDataSource {
    override val profile: Flow<UserProfile> = profileDao.getProfile().map { it?.toDomain() ?: UserProfile() }

    override suspend fun saveProfile(profile: UserProfile) {
        profileDao.saveProfile(profile.toEntity())
    }
}
