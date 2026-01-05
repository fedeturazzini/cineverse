package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.AiSearchGeminiResponse
import com.ft.architectcoders.data.datasource.TmdbQueryPlan
import com.ft.architectcoders.domain.model.ChatMessage
import com.ft.architectcoders.domain.model.DetectedPreferences
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class AiSearchPromptBuilder(private val json: Json) {

    fun buildPrompt(
        conversationHistory: List<ChatMessage>,
        turnIndex: Int,
        region: String,
        topGenreIds: List<Int>,
        avoidMovieIds: List<Int>,
    ): String {
        val historyJson = conversationHistory.joinToString(",\n") { msg ->
            """{"role":"${msg.role.name.lowercase()}","content":"${msg.content.replace("\"", "\\\"")}"}"""
        }

        val isFinalTurn = turnIndex >= 3

        return """
            Eres un recomendador de películas experto. SOLO puedes hablar sobre películas y recomendaciones cinematográficas.
            El usuario tiene un máximo de 3 preguntas. Este es el turno $turnIndex de 3.
            ${if (isFinalTurn) "Este es el ÚLTIMO turno. Debes incluir un resumen final (finalBullets)." else ""}

            REGLAS DE RESPUESTA (MUY IMPORTANTE):
            1. Si el mensaje es un SALUDO o muy vago (hola, qué tal, hey, buenas, etc.):
               - tmdbQueryPlan: null (NO buscar películas)
               - isOutOfScope: false
               - assistantMessage: "¡Hola! ¿Qué tipo de película te gustaría ver? Podés decirme un género, un mood, un actor o director que te guste."

            2. Si el mensaje NO es sobre películas (política, matemáticas, noticias, etc.):
               - tmdbQueryPlan: null
               - isOutOfScope: true
               - assistantMessage: "Solo puedo ayudarte a elegir películas. Decime qué tipo de peli querés ver."

            3. SOLO genera tmdbQueryPlan si el usuario expresa CLARAMENTE qué tipo de película quiere:
               - Menciona un género (acción, comedia, terror, etc.) -> usa type: "DISCOVER"
               - Menciona un mood (algo divertido, triste, emocionante, etc.) -> usa type: "DISCOVER"
               - Menciona un actor, director o película específica -> usa type: "SEARCH" con searchQuery
               - Menciona un año o década -> usa type: "DISCOVER"
               - Hace una solicitud específica de recomendación

            4. TIPOS DE BÚSQUEDA:
               - "DISCOVER": Para filtrar por géneros, año, rating, etc.
               - "SEARCH": Para buscar por título de película, actor o director. Usa searchQuery con el nombre.

            Historial de conversación:
            [$historyJson]

            Señales del usuario:
            - Región: $region
            - Géneros favoritos (IDs TMDB): $topGenreIds
            - IDs de películas a evitar: $avoidMovieIds

            IDs de géneros TMDB: Acción=28, Aventura=12, Animación=16, Comedia=35, Crimen=80, Documental=99, Drama=18, Familia=10751, Fantasía=14, Historia=36, Terror=27, Música=10402, Misterio=9648, Romance=10749, Ciencia Ficción=878, Thriller=53, Guerra=10752, Western=37.

            Responde SOLO con JSON válido, sin texto adicional:
            {
                "assistantMessage": "Respuesta breve y útil (max 200 chars)",
                "isOutOfScope": false,
                "detectedPreferences": null o {
                    "includeGenreIds": [lista de IDs],
                    "excludeGenreIds": [lista de IDs],
                    "yearFrom": número o null,
                    "yearTo": número o null,
                    "maxRuntimeMinutes": número o null,
                    "minVoteAverage": número o null,
                    "keywords": ["palabras clave"],
                    "similarToTitles": ["títulos similares"]
                },
                "tmdbQueryPlan": null o {
                    "type": "DISCOVER" o "SEARCH",
                    "searchQuery": "título o nombre" o null (solo para SEARCH),
                    "genres": [IDs] o null (solo para DISCOVER),
                    "excludeGenres": [IDs] o null,
                    "minVoteAverage": número o null,
                    "yearFrom": número o null,
                    "yearTo": número o null,
                    "sortBy": "popularity.desc" o null
                },
                "followupQuestion": "pregunta opcional para afinar" o null,
                "finalBullets": ${if (isFinalTurn) """["resumen punto 1", "resumen punto 2", "resumen punto 3"]""" else "null"}
            }

            IMPORTANTE: Si no hay criterios claros de búsqueda, tmdbQueryPlan DEBE ser null.
            """.trimIndent()
    }

    fun parseResponse(response: String): AiSearchGeminiResponse {
        val jsonString = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val parsed = json.decodeFromString<GeminiAiSearchResponseDto>(jsonString)
            AiSearchGeminiResponse(
                assistantMessage = parsed.assistantMessage.take(300),
                isOutOfScope = parsed.isOutOfScope,
                detectedPreferences = parsed.detectedPreferences?.let {
                    DetectedPreferences(
                        includeGenreIds = it.includeGenreIds ?: emptyList(),
                        excludeGenreIds = it.excludeGenreIds ?: emptyList(),
                        yearFrom = it.yearFrom,
                        yearTo = it.yearTo,
                        maxRuntimeMinutes = it.maxRuntimeMinutes,
                        minVoteAverage = it.minVoteAverage,
                        keywords = it.keywords ?: emptyList(),
                        similarToTitles = it.similarToTitles ?: emptyList(),
                    )
                },
                tmdbQueryPlan = parsed.tmdbQueryPlan?.let {
                    TmdbQueryPlan(
                        type = it.type ?: "DISCOVER",
                        searchQuery = it.searchQuery,
                        genres = it.genres,
                        excludeGenres = it.excludeGenres,
                        minVoteAverage = it.minVoteAverage,
                        yearFrom = it.yearFrom,
                        yearTo = it.yearTo,
                        sortBy = it.sortBy,
                    )
                },
                followupQuestion = parsed.followupQuestion,
                finalBullets = parsed.finalBullets,
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse AI search response: ${e.message}")
        }
    }

    @Serializable
    private data class GeminiAiSearchResponseDto(
        val assistantMessage: String,
        val isOutOfScope: Boolean = false,
        val detectedPreferences: DetectedPreferencesDto? = null,
        val tmdbQueryPlan: TmdbQueryPlanDto? = null,
        val followupQuestion: String? = null,
        val finalBullets: List<String>? = null,
    )

    @Serializable
    private data class DetectedPreferencesDto(
        val includeGenreIds: List<Int>? = null,
        val excludeGenreIds: List<Int>? = null,
        val yearFrom: Int? = null,
        val yearTo: Int? = null,
        val maxRuntimeMinutes: Int? = null,
        val minVoteAverage: Float? = null,
        val keywords: List<String>? = null,
        val similarToTitles: List<String>? = null,
    )

    @Serializable
    private data class TmdbQueryPlanDto(
        val type: String? = null,
        val searchQuery: String? = null,
        val genres: List<Int>? = null,
        val excludeGenres: List<Int>? = null,
        val minVoteAverage: Float? = null,
        val yearFrom: Int? = null,
        val yearTo: Int? = null,
        val sortBy: String? = null,
    )
}

