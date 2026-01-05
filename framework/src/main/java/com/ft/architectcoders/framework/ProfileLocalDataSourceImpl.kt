package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.domain.model.Profile
import com.ft.architectcoders.framework.database.DbProfile
import com.ft.architectcoders.framework.database.ProfileDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ProfileLocalDataSourceImpl(private val profileDao: ProfileDao) : ProfileLocalDataSource {
    override val profile: Flow<Profile> = profileDao.getProfile().map { it?.toDomain() ?: Profile() }

    override suspend fun saveProfile(profile: Profile) {
        profileDao.saveProfile(profile.toDbProfile())
    }

    private fun DbProfile.toDomain(): Profile {
        val genres =
            if (favoriteGenres.isNotEmpty()) {
                favoriteGenres.split(",").filter { it.isNotBlank() }
            } else {
                emptyList()
            }
        return Profile(
            id = id,
            name = name,
            profilePhotoPath = profilePhotoPath,
            region = region,
            favoriteGenres = genres,
        )
    }

    private fun Profile.toDbProfile(): DbProfile {
        return DbProfile(
            id = id,
            name = name,
            profilePhotoPath = profilePhotoPath,
            region = region,
            favoriteGenres = favoriteGenres.joinToString(","),
        )
    }
}
