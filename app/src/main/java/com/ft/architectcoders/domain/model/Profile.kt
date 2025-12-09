package com.ft.architectcoders.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class Profile(
    @PrimaryKey
    val id: Int = 1,
    val name: String = "",
    val profilePhotoPath: String? = null,
    val region: String = "US",
    val favoriteGenres: String = "",
)

fun Profile.toDomain(): UserProfile {
    val genres = if (favoriteGenres.isNotEmpty()) {
        favoriteGenres.split(",").filter { it.isNotBlank() }
    } else {
        emptyList()
    }
    return UserProfile(
        id = id,
        name = name,
        profilePhotoPath = profilePhotoPath,
        region = region,
        favoriteGenres = genres,
    )
}

fun UserProfile.toEntity(): Profile {
    return Profile(
        id = id,
        name = name,
        profilePhotoPath = profilePhotoPath,
        region = region,
        favoriteGenres = favoriteGenres.joinToString(","),
    )
}