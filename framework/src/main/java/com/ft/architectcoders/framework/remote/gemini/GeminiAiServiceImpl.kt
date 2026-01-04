package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.toGeminiResult
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.TasteFingerprint
import com.ft.architectcoders.domain.Result
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GeminiAiServiceImpl(private val apiKey: String) : GeminiAiService {
    companion object {
        private const val MODEL_NAME = "gemini-3-flash-preview"
    }

    private val model =
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey,
            generationConfig =
                generationConfig {
                    temperature = 0.7f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 200
                },
        )

    private val fingerprintModel =
        GenerativeModel(
            modelName = MODEL_NAME,
            apiKey = apiKey,
            generationConfig =
                generationConfig {
                    temperature = 0.8f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 50000
                },
        )

    override suspend fun generateMovieReview(
        title: String,
        overview: String,
    ): Result<AiReview> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt =
                    """
                    Eres un crítico de cine experto. Analiza esta película y proporciona:
                    1. Una puntuación del 1 al 5 (con decimales, ej: 4.5)
                    2. Una frase corta y cautivadora sobre la película (máximo 80 caracteres)

                    Película: $title
                    Sinopsis: $overview

                    Responde SOLO en este formato exacto:
                    RATING: [número]
                    QUOTE: [frase]
                    """.trimIndent()

                val response = model.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseAiResponse(text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    override suspend fun generateTasteFingerprint(
        sessionId: Long,
        choices: List<DuelChoice>,
    ): Result<TasteFingerprint> {
        return withContext(Dispatchers.IO) {
            try {
                val choicesText = choices.mapIndexed { index, choice ->
                    "${index + 1}. ${choice.winnerTitle} > ${choice.loserTitle}"
                }.joinToString("\n")

                val prompt =
                    """
                    Eres un analista de gustos cinematográficos. Basándote en estas 10 elecciones de un usuario en un duelo de películas, genera un "taste fingerprint".

                    Elecciones (ganador vs perdedor):
                    $choicesText

                    Analiza patrones en: géneros, épocas, estilos narrativos, atmósfera, tipo de protagonistas.

                    Responde SOLO en este formato exacto:
                    INSIGHTS:
                    - [insight 1, max 60 chars]
                    - [insight 2, max 60 chars]
                    - [insight 3, max 60 chars]
                    - [insight 4, max 60 chars]
                    - [insight 5, max 60 chars]
                    TRAITS: [trait1], [trait2], [trait3]
                    """.trimIndent()

                val response = fingerprintModel.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseFingerprintResponse(sessionId, text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    private fun parseAiResponse(response: String): AiReview {
        val lines = response.lines()
        val rating =
            lines
                .find { it.startsWith("RATING:") }
                ?.substringAfter("RATING:")
                ?.trim()
                ?.toFloatOrNull() ?: 4.0f

        val quote =
            lines
                .find { it.startsWith("QUOTE:") }
                ?.substringAfter("QUOTE:")
                ?.trim() ?: "Una película fascinante"

        return AiReview(
            rating = rating.coerceIn(0f, 5f),
            quote = quote.take(100),
        )
    }

    private fun parseFingerprintResponse(sessionId: Long, response: String): TasteFingerprint {
        val lines = response.lines()

        val insights = mutableListOf<String>()
        var inInsightsSection = false

        for (line in lines) {
            val trimmedLine = line.trim()
            when {
                trimmedLine.startsWith("INSIGHTS:") -> inInsightsSection = true
                trimmedLine.startsWith("TRAITS:") -> inInsightsSection = false
                inInsightsSection && trimmedLine.startsWith("-") -> {
                    val insight = trimmedLine.removePrefix("-").trim()
                    if (insight.isNotBlank()) {
                        insights.add(insight.take(60))
                    }
                }
            }
        }

        val traitsLine = lines.find { it.trim().startsWith("TRAITS:") }
        val traits = traitsLine
            ?.substringAfter("TRAITS:")
            ?.split(",")
            ?.map { it.trim() }
            ?.filter { it.isNotBlank() }
            ?: emptyList()

        return TasteFingerprint(
            sessionId = sessionId,
            insights = insights.ifEmpty { listOf("Te gustan las buenas películas") },
            dominantTraits = traits.ifEmpty { listOf("ecléctico") },
            generatedByAi = true,
        )
    }
}
