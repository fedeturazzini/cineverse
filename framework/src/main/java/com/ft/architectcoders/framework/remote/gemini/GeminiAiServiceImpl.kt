package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.ChallengeAiResult
import com.ft.architectcoders.data.datasource.ChallengeFilters
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.MarathonAiPick
import com.ft.architectcoders.data.datasource.MarathonAiResult
import com.ft.architectcoders.data.toGeminiResult
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.ChallengeType
import com.ft.architectcoders.domain.model.DuelChoice
import com.ft.architectcoders.domain.model.GenreWeight
import com.ft.architectcoders.domain.model.MarathonCandidate
import com.ft.architectcoders.domain.model.MarathonTheme
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
                    temperature = 0.8f
                    topK = 40
                    topP = 0.95f
                    maxOutputTokens = 500000
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
                val choicesText =
                    choices.mapIndexed { index, choice ->
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

    private fun parseFingerprintResponse(
        sessionId: Long,
        response: String,
    ): TasteFingerprint {
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
        val traits =
            traitsLine
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
        val jsonString =
            response
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

    override suspend fun generateMarathonPlan(
        theme: MarathonTheme,
        candidates: List<MarathonCandidate>,
        topGenres: List<Int>,
        region: String,
    ): Result<MarathonAiResult> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildMarathonPrompt(theme, candidates, topGenres, region)
                val response = model.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseMarathonResponse(text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    private fun buildMarathonPrompt(
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

    private fun parseMarathonResponse(response: String): MarathonAiResult {
        val jsonString =
            response
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

    override suspend fun generateDailyChallenge(
        date: String,
        topGenres: List<Pair<Int, Int>>,
        decadeHistogram: List<Pair<Int, Int>>,
        favoriteMovieIds: List<Int>,
    ): Result<ChallengeAiResult> {
        return withContext(Dispatchers.IO) {
            try {
                val prompt = buildChallengePrompt(date, topGenres, decadeHistogram, favoriteMovieIds)
                val response = model.generateContent(prompt)
                val text = response.text ?: throw Exception("No response from AI")

                Result.Success(parseChallengeResponse(text))
            } catch (e: Exception) {
                e.toGeminiResult()
            }
        }
    }

    private fun buildChallengePrompt(
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

    private fun parseChallengeResponse(response: String): ChallengeAiResult {
        val jsonString =
            response
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
