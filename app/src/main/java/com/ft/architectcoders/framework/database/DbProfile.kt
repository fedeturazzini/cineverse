package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "profile")
data class DbProfile(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val name: String,
    val profilePhotoPath: String?,
    val region: String,
    val favoriteGenres: String,
)
