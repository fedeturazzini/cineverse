package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_challenges")
data class DbDailyChallenge(
    @PrimaryKey val date: String,
    val type: String,
    val title: String,
    val reason: String,
    val rules: String, // JSON array as string
    val badgeId: String,
    val badgeName: String,
    val badgeEmoji: String,
    val movieOptionsJson: String, // JSON array as string
    val status: String,
    val completedMovieId: Int?,
    val completedAt: Long?,
    val generatedByAi: Boolean,
)

@Entity(tableName = "challenge_badges")
data class DbChallengeBadge(
    @PrimaryKey val id: String,
    val name: String,
    val emoji: String,
    val unlockedAt: Long,
)

