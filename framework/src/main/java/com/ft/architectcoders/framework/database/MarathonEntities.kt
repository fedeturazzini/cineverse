package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "marathons")
data class DbMarathon(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val themeId: String,
    val themeTitle: String,
    val tagline: String,
    val generatedByAi: Boolean,
    val timestamp: Long,
)

@Entity(
    tableName = "marathon_picks",
    foreignKeys = [
        ForeignKey(
            entity = DbMarathon::class,
            parentColumns = ["id"],
            childColumns = ["marathonId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("marathonId")],
)
data class DbMarathonPick(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val marathonId: Long,
    val movieId: Int,
    val movieTitle: String,
    val moviePoster: String?,
    val movieYear: String?,
    val movieRating: Double?,
    val order: Int,
    val why: String,
    val warnings: String, // JSON array as string
)

