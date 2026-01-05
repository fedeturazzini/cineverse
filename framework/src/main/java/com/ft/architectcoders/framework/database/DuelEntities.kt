package com.ft.architectcoders.framework.database

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "duel_sessions")
data class DbDuelSession(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val timestamp: Long,
    val isCompleted: Boolean,
)

@Entity(
    tableName = "duel_choices",
    foreignKeys = [
        ForeignKey(
            entity = DbDuelSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class DbDuelChoice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val winnerId: Int,
    val loserId: Int,
    val winnerTitle: String,
    val loserTitle: String,
    val roundNumber: Int,
)

@Entity(
    tableName = "taste_fingerprints",
    foreignKeys = [
        ForeignKey(
            entity = DbDuelSession::class,
            parentColumns = ["id"],
            childColumns = ["sessionId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index("sessionId")],
)
data class DbTasteFingerprint(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long,
    val insights: String,
    val dominantTraits: String,
    val generatedByAi: Boolean,
)
