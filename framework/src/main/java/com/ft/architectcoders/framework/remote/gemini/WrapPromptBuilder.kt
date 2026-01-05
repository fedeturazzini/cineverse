package com.ft.architectcoders.framework.remote.gemini

import com.ft.architectcoders.data.datasource.WrapAiResult
import com.ft.architectcoders.domain.model.WrapGeminiInput
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

class WrapPromptBuilder(private val json: Json) {

    fun buildPrompt(input: WrapGeminiInput): String {
        val stats = input.stats
        val period = input.period

        val topGenresJson = stats.topGenres.take(5).joinToString(",") { genre ->
            """{"id":${genre.id},"name":"${genre.name}","count":${genre.count}}"""
        }

        val topMoviesJson = stats.topMovies.take(10).joinToString(",") { movie ->
            """{"id":${movie.id},"title":"${escapeJson(movie.title)}","year":${movie.year},"score":${movie.score}}"""
        }

        val favoritesJson = stats.favoritesHighlights.take(5).joinToString(",") { movie ->
            """{"id":${movie.id},"title":"${escapeJson(movie.title)}"}"""
        }

        val topActorsJson = stats.topActors.take(3).joinToString(",") { person ->
            """{"id":${person.id},"name":"${escapeJson(person.name)}","count":${person.count}}"""
        }

        val topDirectorsJson = stats.topDirectors.take(3).joinToString(",") { person ->
            """{"id":${person.id},"name":"${escapeJson(person.name)}","count":${person.count}}"""
        }

        val aiSearchJson = stats.aiSearchHighlights?.let { highlights ->
            val intents = highlights.topIntents.take(3).joinToString(",") { "\"${escapeJson(it)}\"" }
            val keywords = highlights.topKeywords.take(5).joinToString(",") { "\"${escapeJson(it)}\"" }
            """{"topIntents":[$intents],"topKeywords":[$keywords]}"""
        } ?: "null"

        return """
            Eres un experto en generar contenido divertido y shareable sobre perfiles cinematográficos.
            
            Genera un "wrap" estilo resumen para este usuario basándote en sus datos.

            DATOS DEL USUARIO:
            {
                "period": {"from":"${period.fromDate}","to":"${period.toDate}"},
                "profile": {
                    "name": ${input.profileName?.let { "\"$it\"" } ?: "null"},
                    "region": ${input.profileRegion?.let { "\"$it\"" } ?: "null"}
                },
                "userStats": {
                    "totals": {"moviesViewed":${stats.totals.moviesViewed},"favorites":${stats.totals.favorites},"aiSearchSessions":${stats.totals.aiSearchSessions}},
                    "topGenres": [$topGenresJson],
                    "topMovies": [$topMoviesJson],
                    "favoritesHighlights": [$favoritesJson],
                    "topActors": [$topActorsJson],
                    "topDirectors": [$topDirectorsJson],
                    "aiSearchHighlights": $aiSearchJson
                }
            }

            RESTRICCIONES IMPORTANTES:
            1. NO inventes datos que no estén en el payload
            2. NO hagas inferencias personales ni uses datos sensibles
            3. Tono divertido, amable y shareable - NO agresivo ni ofensivo
            4. maxFunnyProfileSummaryChars: 220
            5. maxArchetypeNameWords: 4
            6. maxTaglineChars: 120
            7. maxBulletChars: 90 por bullet (exactamente 3 bullets)
            8. maxShareTextChars: 220

            INSTRUCCIONES:
            1. "funnyProfileSummary": Un resumen divertido del perfil (1-3 líneas, tipo bio de red social)
            2. "archetype": Define un arquetipo único (nombre creativo de max 4 palabras + tagline + 3 bullets)
            3. "shareText": Texto corto listo para compartir en redes (incluí #Cineverso60s)
            4. "sectionCopy": Líneas cortas para cada sección de la UI

            Si el usuario tiene datos de actores/directores, incluí frases divertidas sobre ellos.
            Si tiene datos de búsquedas IA, hacé un highlight gracioso.

            Responde SOLO con JSON válido, sin texto adicional:
            {
                "funnyProfileSummary": "texto divertido...",
                "archetype": {
                    "name": "Nombre Creativo",
                    "tagline": "Una frase que define al usuario...",
                    "bullets": ["bullet 1", "bullet 2", "bullet 3"]
                },
                "shareText": "Texto para compartir #Cineverso60s",
                "sectionCopy": {
                    "genresLine": "Tus géneros top: ...",
                    "moviesLine": "Tus infaltables:",
                    "aiLine": "Pediste: ...",
                    "actorLine": "Tu actor/actriz favorito/a: ...",
                    "directorLine": "Tu director/a favorito/a: ..."
                }
            }
            """.trimIndent()
    }

    fun parseResponse(response: String): WrapAiResult {
        val jsonString = response
            .replace("```json", "")
            .replace("```", "")
            .trim()

        return try {
            val parsed = json.decodeFromString<GeminiWrapResponse>(jsonString)
            WrapAiResult(
                funnyProfileSummary = parsed.funnyProfileSummary.take(220),
                archetypeName = parsed.archetype.name.split(" ").take(4).joinToString(" "),
                archetypeTagline = parsed.archetype.tagline.take(120),
                archetypeBullets = parsed.archetype.bullets.take(3).map { it.take(90) },
                shareText = parsed.shareText.take(220),
                genresLine = parsed.sectionCopy.genresLine,
                moviesLine = parsed.sectionCopy.moviesLine,
                aiLine = parsed.sectionCopy.aiLine,
                actorLine = parsed.sectionCopy.actorLine,
                directorLine = parsed.sectionCopy.directorLine,
            )
        } catch (e: Exception) {
            throw Exception("Failed to parse wrap response: ${e.message}")
        }
    }

    private fun escapeJson(text: String): String {
        return text
            .replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r")
            .replace("\t", "\\t")
    }

    @Serializable
    private data class GeminiWrapResponse(
        val funnyProfileSummary: String,
        val archetype: GeminiArchetype,
        val shareText: String,
        val sectionCopy: GeminiSectionCopy,
    )

    @Serializable
    private data class GeminiArchetype(
        val name: String,
        val tagline: String,
        val bullets: List<String>,
    )

    @Serializable
    private data class GeminiSectionCopy(
        val genresLine: String? = null,
        val moviesLine: String? = null,
        val aiLine: String? = null,
        val actorLine: String? = null,
        val directorLine: String? = null,
    )
}
