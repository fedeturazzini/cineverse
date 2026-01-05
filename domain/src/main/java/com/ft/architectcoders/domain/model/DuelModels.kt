package com.ft.architectcoders.domain.model

data class DuelChoice(
    val winnerId: Int,
    val loserId: Int,
    val winnerTitle: String,
    val loserTitle: String,
    val roundNumber: Int,
)

data class DuelSession(
    val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val choices: List<DuelChoice> = emptyList(),
    val isCompleted: Boolean = false,
)

data class TasteFingerprint(
    val id: Long = 0,
    val sessionId: Long,
    val timestamp: Long = System.currentTimeMillis(),
    val insights: List<String>,
    val dominantTraits: List<String>,
    val generatedByAi: Boolean = true,
)
