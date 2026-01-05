package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.MarathonAiPick
import com.ft.architectcoders.data.datasource.MarathonAiResult
import com.ft.architectcoders.domain.model.MarathonCandidate
import com.ft.architectcoders.domain.model.MarathonTheme
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class MarathonPromptBuilder(private val json: Json) {

    fun buildPrompt(
        theme: MarathonTheme,
        candidates: List<MarathonCandidate>,
        topGenres: List<Int>,
        region: String,
    ): String {
        val candidatesJson = candidates.take(30).joinToString(",\n") { c ->
            """{"id":${c.id},"title":"${c.title.replace("\"", "\\\"")}","genreIds":${c.genreIds},"voteAverage":${c.voteAverage},"year":"${c.year ?: ""}"}"""
        }

        return """
            Eres un curador de maratones de películas. Selecciona ${theme.targetItems} películas para una maratón temática.

            TEMA: "${theme.title}" (${theme.emoji})
            Duración máxima total: ${theme.maxTotalMinutes} minutos

            Señales del usuario:
            - Géneros favoritos (IDs TMDB): $topGenres
            - Región: $region

            Candidatas disponibles:
            [$candidatesJson]

            INSTRUCCIONES:
            1. Selecciona EXACTAMENTE ${theme.targetItems} películas de la lista de candidatas
            2. Ordénalas en el orden ideal para ver durante la maratón
            3. Explica brevemente por qué cada una encaja con el tema (max 100 chars)
            4. Si hay advertencias (película lenta, muy larga, intensa), inclúyelas

            Responde SOLO con JSON válido, sin texto adicional:
            {
                "tagline": "frase corta y atractiva sobre esta maratón, max 120 chars",
                "picks": [
                    {"movieId": 123, "order": 1, "why": "razón corta", "warnings": []},
                    {"movieId": 456, "order": 2, "why": "razón corta", "warnings": ["lenta"]}
                ]
            }

            Los movieId DEBEN ser de la lista de candidatas.
            """.trimIndent()
    }

    fun parseResponse(response: String): MarathonAiResult {
        val jsonString = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val parsed = json.decodeFromString<GeminiMarathonResponse>(jsonString)
            MarathonAiResult(
                tagline = parsed.tagline.take(120),
                picks = parsed.picks.mapIndexed { index, pick ->
                    MarathonAiPick(
                        movieId = pick.movieId,
                        order = pick.order ?: (index + 1),
                        why = pick.why.take(100),
                        warnings = pick.warnings ?: emptyList(),
                    )
                },
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse marathon response: ${e.message}")
        }
    }

    @Serializable
    private data class GeminiMarathonResponse(
        val tagline: String,
        val picks: List<GeminiMarathonPick>,
    )

    @Serializable
    private data class GeminiMarathonPick(
        val movieId: Int,
        val order: Int? = null,
        val why: String,
        val warnings: List<String>? = null,
    )
}

