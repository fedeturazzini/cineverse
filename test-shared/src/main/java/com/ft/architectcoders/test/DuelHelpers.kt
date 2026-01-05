package com.ft.architectcoders.test

import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint

fun sampleDuelChoice(
    roundNumber: Int = 1,
    winnerId: Int = 1,
    loserId: Int = 2,
    winnerTitle: String = "Winner Movie $roundNumber",
    loserTitle: String = "Loser Movie $roundNumber",
) = DuelChoice(
    winnerId = winnerId,
    loserId = loserId,
    winnerTitle = winnerTitle,
    loserTitle = loserTitle,
    roundNumber = roundNumber,
)

fun sampleDuelChoices(count: Int = 10) =
    (1..count).map { round ->
        sampleDuelChoice(
            roundNumber = round,
            winnerId = round * 2 - 1,
            loserId = round * 2,
        )
    }

fun sampleDuelSession(
    id: Long = 1L,
    timestamp: Long = System.currentTimeMillis(),
    choicesCount: Int = 10,
    isCompleted: Boolean = false,
) = DuelSession(
    id = id,
    timestamp = timestamp,
    choices = sampleDuelChoices(choicesCount),
    isCompleted = isCompleted,
)

fun sampleTasteFingerprint(
    id: Long = 1L,
    sessionId: Long = 1L,
    insights: List<String> =
        listOf(
            "Te gustan los dramas intensos",
            "Prefieres personajes complejos",
            "Disfrutas narrativas lentas",
            "Te atraen las atmósferas oscuras",
            "Valoras la cinematografía artística",
        ),
    dominantTraits: List<String> = listOf("cinéfilo", "introspectivo", "atmosférico"),
    generatedByAi: Boolean = true,
) = TasteFingerprint(
    id = id,
    sessionId = sessionId,
    insights = insights,
    dominantTraits = dominantTraits,
    generatedByAi = generatedByAi,
)
