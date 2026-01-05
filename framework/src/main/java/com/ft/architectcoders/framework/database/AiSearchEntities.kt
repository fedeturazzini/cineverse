package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ai_search_sessions")
data class DbAiSearchSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val createdAt: Long,
    val messagesJson: String,
    val preferencesJson: String?,
    val recommendedMovieIds: String,
    val finalBulletsJson: String?,
    val isCompleted: Boolean,
)

