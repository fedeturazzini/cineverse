package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.domain.model.GenreWeight
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MoodPromptBuilder(private val json: Json) {

    fun buildPrompt(moodVector: MoodVector): String {
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

    fun parseResponse(response: String): MoodProfile {
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

