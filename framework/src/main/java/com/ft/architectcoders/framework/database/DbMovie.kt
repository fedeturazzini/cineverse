package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DbMovie(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    val title: String,
    val originalTitle: String,
    val poster: String,
    val backdrop: String?,
    val releaseDate: String,
    val overview: String,
    val favorite: Boolean,
    val aiRating: Float?,
    val aiQuote: String?,
)
