package com.ft.architectcoders.data.repository.wrap

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.WrapAiResult
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.domain.model.WrapArchetype
import com.ft.architectcoders.domain.model.WrapGeminiInput
import com.ft.architectcoders.domain.model.WrapSectionCopy

class WrapRepositoryImpl(
    private val geminiAiService: GeminiAiService,
) : WrapRepository {

    override suspend fun generateWrap(input: WrapGeminiInput): Result<CineverseWrap> {
        return when (val result = geminiAiService.generateCineverseWrap(input)) {
            is Result.Success -> {
                Result.Success(buildWrapFromAiResult(input, result.data, generatedByAi = true))
            }
            is Result.Error -> {
                // Fallback to deterministic generation
                Result.Success(buildDeterministicWrap(input))
            }
            is Result.Loading -> Result.Loading
        }
    }

    private fun buildWrapFromAiResult(
        input: WrapGeminiInput,
        aiResult: WrapAiResult,
        generatedByAi: Boolean,
    ): CineverseWrap {
        return CineverseWrap(
            period = input.period,
            funnyProfileSummary = aiResult.funnyProfileSummary,
            archetype = WrapArchetype(
                name = aiResult.archetypeName,
                tagline = aiResult.archetypeTagline,
                bullets = aiResult.archetypeBullets,
            ),
            sectionCopy = WrapSectionCopy(
                genresLine = aiResult.genresLine,
                moviesLine = aiResult.moviesLine,
                aiLine = aiResult.aiLine,
                actorLine = aiResult.actorLine,
                directorLine = aiResult.directorLine,
            ),
            shareText = aiResult.shareText,
            stats = input.stats,
            generatedByAi = generatedByAi,
        )
    }

    private fun buildDeterministicWrap(input: WrapGeminiInput): CineverseWrap {
        val stats = input.stats
        val topGenre = stats.topGenres.firstOrNull()?.name ?: "variado"

        // Deterministic archetype based on genre dominance
        val archetype = determineArchetype(stats.topGenres.map { it.name.lowercase() })

        val funnyProfileSummary = buildString {
            append("Tu Cineverso es $topGenre puro. ")
            if (stats.totals.favorites > 0) {
                append("Sos de los que guardan favoritos como si fueran trofeos. ")
            }
            if (stats.totals.moviesViewed > 10) {
                append("Una máquina de ver películas.")
            }
        }.take(220)

        val shareText = buildString {
            append("Mi arquetipo cinéfilo: ${archetype.name}. ")
            append("Mi género top: $topGenre. ")
            append("#Cineverso60s")
        }.take(220)

        return CineverseWrap(
            period = input.period,
            funnyProfileSummary = funnyProfileSummary,
            archetype = archetype,
            sectionCopy = WrapSectionCopy(
                genresLine = stats.topGenres.take(3).joinToString(", ") { it.name }.let { "Tus géneros top: $it" },
                moviesLine = if (stats.topMovies.isNotEmpty()) "Tus infaltables:" else null,
                aiLine = stats.aiSearchHighlights?.topKeywords?.take(3)?.joinToString(", ")?.let { "Pediste: $it" },
                actorLine = stats.topActors.firstOrNull()?.let { "Tu actor favorito: ${it.name}" },
                directorLine = stats.topDirectors.firstOrNull()?.let { "Tu director favorito: ${it.name}" },
            ),
            shareText = shareText,
            stats = stats,
            generatedByAi = false,
        )
    }

    private fun determineArchetype(genres: List<String>): WrapArchetype {
        val hasThrillerMystery = genres.any { it in listOf("thriller", "misterio", "mystery", "crimen", "crime") }
        val hasComedy = genres.any { it in listOf("comedia", "comedy") }
        val hasSciFi = genres.any { it in listOf("ciencia ficción", "sci-fi", "science fiction") }
        val hasDrama = genres.any { it in listOf("drama") }
        val hasAction = genres.any { it in listOf("acción", "action") }
        val hasHorror = genres.any { it in listOf("terror", "horror") }

        return when {
            hasThrillerMystery -> WrapArchetype(
                name = "Adicto al Plot Twist",
                tagline = "Nada te sorprende... o eso creés. Siempre buscás esa vuelta de tuerca.",
                bullets = listOf(
                    "Detectás al asesino antes del minuto 30",
                    "Tu frase favorita: 'Ya lo sabía'",
                    "Revisás teorías en Reddit después de cada peli",
                ),
            )
            hasComedy -> WrapArchetype(
                name = "Cazador de Risas",
                tagline = "La vida es mejor con humor. Tu lista de pelis lo demuestra.",
                bullets = listOf(
                    "Citás películas en conversaciones diarias",
                    "Tu emoji más usado: 😂",
                    "Maratones de comedias son tu terapia",
                ),
            )
            hasSciFi -> WrapArchetype(
                name = "Astronauta Nocturno",
                tagline = "Explorás galaxias desde tu sillón. El futuro es tu presente.",
                bullets = listOf(
                    "Debatís sobre paradojas temporales",
                    "Tu playlist incluye sintetizadores",
                    "Soñás con colonizar Marte",
                ),
            )
            hasHorror -> WrapArchetype(
                name = "Domador del Terror",
                tagline = "El miedo es tu adrenalina. Lo que asusta a otros, a vos te entretiene.",
                bullets = listOf(
                    "Ves pelis de terror para relajarte",
                    "Te reís en las escenas de suspenso",
                    "Halloween es tu Navidad",
                ),
            )
            hasAction -> WrapArchetype(
                name = "Buscador de Adrenalina",
                tagline = "Explosiones, persecuciones y héroes. Tu corazón late a 120 bpm.",
                bullets = listOf(
                    "Subís el volumen en las escenas de acción",
                    "Tu héroe favorito tiene catchphrase",
                    "Las secuencias de pelea son arte",
                ),
            )
            hasDrama -> WrapArchetype(
                name = "Coleccionista de Emociones",
                tagline = "Las historias profundas son tu especialidad. Sentir es vivir.",
                bullets = listOf(
                    "Tenés pañuelos listos siempre",
                    "Las actuaciones te importan más que los efectos",
                    "Recomendás pelis que 'cambian vidas'",
                ),
            )
            else -> WrapArchetype(
                name = "Explorador Ecléctico",
                tagline = "No te casás con ningún género. Tu gusto es tu superpoder.",
                bullets = listOf(
                    "Tu lista de pelis parece aleatoria",
                    "Siempre tenés una recomendación",
                    "Probás cosas nuevas sin prejuicios",
                ),
            )
        }
    }
}
