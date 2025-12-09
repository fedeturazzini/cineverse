package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.data.datasource.database.ProfileDao
import com.ft.architectcoders.domain.model.UserProfile
import com.ft.architectcoders.domain.model.toDomain
import com.ft.architectcoders.domain.model.toEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileLocalDataSource(private val profileDao: ProfileDao) {
    val profile: Flow<UserProfile> = profileDao.getProfile().map { it?.toDomain() ?: UserProfile() }

    suspend fun saveProfile(profile: UserProfile) {
        profileDao.saveProfile(profile.toEntity())
    }
}
