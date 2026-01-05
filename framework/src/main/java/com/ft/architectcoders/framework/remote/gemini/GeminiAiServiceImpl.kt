package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.toGeminiResult
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.GenreWeight
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.TasteFingerprint
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.generationConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

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

                val response = model.generateContent(prompt)
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

    override suspend fun generateMoodProfile(moodVector: MoodVector): Result<MoodProfile> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildMoodPrompt(moodVector)
                val response = model.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseMoodProfileResponse(text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    private fun buildMoodPrompt(moodVector: MoodVector): String {
        return """
            Eres un experto en recomendaciones cinematográficas. El usuario tiene este perfil de mood (valores 0-100):
            - Energía: ${moodVector.energy}
            - Humor: ${moodVector.humor}
            - Tensión: ${moodVector.tension}
            - Romance: ${moodVector.romance}
            - Cerebral: ${moodVector.cerebral}

            Genera un perfil de recomendación de películas. Responde SOLO con JSON válido:
            {
                "microCopy": "frase corta y divertida describiendo el mood, max 120 chars",
                "tmdbQuery": {
                    "primaryGenreIds": [lista de IDs de géneros TMDB principales],
                    "excludeGenreIds": [géneros a evitar],
                    "sortBy": "popularity.desc",
                    "minVoteAverage": 6.0,
                    "yearFrom": 1990,
                    "yearTo": 2025
                },
                "explanations": ["razón 1", "razón 2", "razón 3"]
            }

            IDs de géneros TMDB: Acción=28, Aventura=12, Animación=16, Comedia=35, Crimen=80, Documental=99, Drama=18, Familia=10751, Fantasía=14, Historia=36, Terror=27, Música=10402, Misterio=9648, Romance=10749, Ciencia Ficción=878, Thriller=53, Guerra=10752, Western=37.

            NO incluyas texto adicional, solo el JSON.
        """.trimIndent()
    }

    private val json = Json { ignoreUnknownKeys = true }

    private fun parseMoodProfileResponse(response: String): MoodProfile {
        val jsonString = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val parsed = json.decodeFromString<GeminiMoodResponse>(jsonString)
            MoodProfile(
                microCopy = parsed.microCopy.take(120),
                genres = parsed.tmdbQuery.primaryGenreIds.map { GenreWeight(it, 1.0f) },
                excludeGenres = parsed.tmdbQuery.excludeGenreIds,
                globalExplanation = parsed.explanations.take(3),
                sortBy = parsed.tmdbQuery.sortBy,
                minVoteAverage = parsed.tmdbQuery.minVoteAverage,
                yearFrom = parsed.tmdbQuery.yearFrom,
                yearTo = parsed.tmdbQuery.yearTo,
                generatedByAi = true,
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse mood profile: ${e.message}")
        }
    }

    @Serializable
    private data class GeminiMoodResponse(
        val microCopy: String,
        val tmdbQuery: TmdbQueryResponse,
        val explanations: List<String>,
    )

    @Serializable
    private data class TmdbQueryResponse(
        val primaryGenreIds: List<Int>,
        val excludeGenreIds: List<Int> = emptyList(),
        val sortBy: String = "popularity.desc",
        val minVoteAverage: Float? = null,
        val yearFrom: Int? = null,
        val yearTo: Int? = null,
    )
}
