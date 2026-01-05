package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.ChallengeAiResult
import com.ft.architectcoders.data.datasource.ChallengeFilters
import com.ft.architectcoders.domain.model.ChallengeType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class ChallengePromptBuilder(private val json: Json) {

    fun buildPrompt(
        date: String,
        topGenres: List<Pair<Int, Int>>,
        decadeHistogram: List<Pair<Int, Int>>,
        favoriteMovieIds: List<Int>,
    ): String {
        val genresJson = topGenres.joinToString(",") { """{"id":${it.first},"count":${it.second}}""" }
        val decadesJson = decadeHistogram.joinToString(",") { """{"decade":${it.first},"count":${it.second}}""" }

        return """
            Eres un experto en gamificación cinematográfica. Genera UN reto del día para este usuario.

            FECHA: $date
            SEÑALES DEL USUARIO:
            - Géneros favoritos: [$genresJson]
            - Histograma de décadas: [$decadesJson]
            - IDs de películas favoritas: $favoriteMovieIds

            TIPOS DE RETO DISPONIBLES:
            - NEW_GENRE: Probar un género que el usuario no suele ver
            - PRE_2000: Ver una película clásica anterior al año 2000
            - NO_SEQUELS_WEEK: Ver películas originales sin secuelas
            - CLASSIC_AWARD_WINNER: Ver un clásico premiado de alta calificación

            INSTRUCCIONES:
            1. Analiza las señales y elige el tipo de reto más apropiado
            2. Si el usuario ve mayormente películas recientes -> PRE_2000
            3. Si el usuario repite géneros -> NEW_GENRE
            4. Genera un título corto y atractivo (max 40 chars)
            5. Explica brevemente por qué este reto es bueno para el usuario (max 100 chars)
            6. Define reglas claras
            7. Asigna un badge temático

            Responde SOLO con JSON válido:
            {
                "challenge": {
                    "type": "PRE_2000|NEW_GENRE|NO_SEQUELS_WEEK|CLASSIC_AWARD_WINNER",
                    "title": "Reto: Título corto",
                    "reason": "Razón personalizada",
                    "rules": ["regla 1", "regla 2"],
                    "badge": {"id": "badge_xxx", "name": "Nombre Badge", "emoji": "🎬"}
                },
                "filters": {
                    "includeGenreIds": [28, 12] o null,
                    "excludeGenreIds": [35] o null,
                    "yearFrom": 1990 o null,
                    "yearTo": 1999 o null,
                    "minVoteAverage": 7.0 o null
                },
                "copy": {
                    "cardSubtitle": "Subtítulo para la card",
                    "completionCopy": "Mensaje al completar"
                }
            }

            IDs de géneros TMDB: Acción=28, Aventura=12, Animación=16, Comedia=35, Crimen=80, Documental=99, Drama=18, Familia=10751, Fantasía=14, Historia=36, Terror=27, Música=10402, Misterio=9648, Romance=10749, Ciencia Ficción=878, Thriller=53, Guerra=10752, Western=37.
            """.trimIndent()
    }

    fun parseResponse(response: String): ChallengeAiResult {
        val jsonString = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val parsed = json.decodeFromString<GeminiChallengeResponse>(jsonString)
            ChallengeAiResult(
                type = ChallengeType.valueOf(parsed.challenge.type),
                title = parsed.challenge.title.take(60),
                reason = parsed.challenge.reason.take(140),
                rules = parsed.challenge.rules,
                badgeId = parsed.challenge.badge.id,
                badgeName = parsed.challenge.badge.name,
                badgeEmoji = parsed.challenge.badge.emoji,
                filters = ChallengeFilters(
                    includeGenreIds = parsed.filters.includeGenreIds,
                    excludeGenreIds = parsed.filters.excludeGenreIds,
                    yearFrom = parsed.filters.yearFrom,
                    yearTo = parsed.filters.yearTo,
                    minVoteAverage = parsed.filters.minVoteAverage,
                ),
                cardSubtitle = parsed.copy.cardSubtitle.take(60),
                completionCopy = parsed.copy.completionCopy.take(100),
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse challenge response: ${e.message}")
        }
    }

    @Serializable
    private data class GeminiChallengeResponse(
        val challenge: GeminiChallengeData,
        val filters: GeminiChallengeFilters,
        val copy: GeminiChallengeCopy,
    )

    @Serializable
    private data class GeminiChallengeData(
        val type: String,
        val title: String,
        val reason: String,
        val rules: List<String>,
        val badge: GeminiChallengeBadge,
    )

    @Serializable
    private data class GeminiChallengeBadge(
        val id: String,
        val name: String,
        val emoji: String,
    )

    @Serializable
    private data class GeminiChallengeFilters(
        val includeGenreIds: List<Int>? = null,
        val excludeGenreIds: List<Int>? = null,
        val yearFrom: Int? = null,
        val yearTo: Int? = null,
        val minVoteAverage: Float? = null,
    )

    @Serializable
    private data class GeminiChallengeCopy(
        val cardSubtitle: String,
        val completionCopy: String,
    )
}

