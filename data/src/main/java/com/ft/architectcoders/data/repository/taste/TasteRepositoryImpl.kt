package com.ft.architectcoders.data.repository.taste

import com.ft.architectcoders.data.datasource.DuelLocalDataSource
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.DuelSession
import com.ft.architectcoders.domain.model.TasteFingerprint
import kotlinx.coroutines.flow.Flow

class TasteRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val duelLocalDataSource: DuelLocalDataSource,
) : TasteRepository {

    override suspend fun generateFingerprint(session: DuelSession): Result<TasteFingerprint> {
        val aiResult = geminiAiService.generateTasteFingerprint(session.id, session.choices)

        return when (aiResult) {
            is Result.Success -> {
                duelLocalDataSource.saveTasteFingerprint(aiResult.data)
                aiResult
            }
            is Result.Error -> {
                val fallback = generateFallbackFingerprint(session)
                duelLocalDataSource.saveTasteFingerprint(fallback)
                Result.Success(fallback)
            }
            is Result.Loading -> aiResult
        }
    }

    override fun getLastFingerprint(): Flow<TasteFingerprint?> {
        return duelLocalDataSource.getLastFingerprint()
    }

    private fun generateFallbackFingerprint(session: DuelSession): TasteFingerprint {
        val choices = session.choices
        val winnerTitles = choices.map { it.winnerTitle }

        val insights = mutableListOf<String>()

        insights.add("🏆 Elegiste: ${winnerTitles.take(3).joinToString(", ")}")

        if (winnerTitles.size > 3) {
            insights.add("También preferiste: ${winnerTitles.drop(3).take(3).joinToString(", ")}")
        }
        val notableLoser = choices.lastOrNull()?.loserTitle
        if (notableLoser != null) {
            insights.add("Descartaste películas como \"$notableLoser\"")
        }

        insights.add("Completaste ${choices.size} rondas de duelo")

        val traits = winnerTitles.take(3)

        return TasteFingerprint(
            sessionId = session.id,
            insights = insights.take(5),
            dominantTraits = traits,
            generatedByAi = false,
        )
    }
}

