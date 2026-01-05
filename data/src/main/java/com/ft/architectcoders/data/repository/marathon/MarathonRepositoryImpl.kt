package com.ft.architectcoders.data.repository.marathon

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.MarathonLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError.*
import com.ft.architectcoders.domain.model.MarathonCandidate
import com.ft.architectcoders.domain.model.MarathonHistoryItem
import com.ft.architectcoders.domain.model.MarathonPick
import com.ft.architectcoders.domain.model.MarathonPlan
import com.ft.architectcoders.domain.model.MarathonTheme
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.Flow

class MarathonRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val marathonLocalDataSource: MarathonLocalDataSource,
) : MarathonRepository {

    override fun getThemes(): List<MarathonTheme> = listOf(
        MarathonTheme(MarathonThemeId.LAUGH, "Quiero reírme", "😂", 3, 360),
        MarathonTheme(MarathonThemeId.CRY, "Quiero llorar", "😢", 2, 300),
        MarathonTheme(MarathonThemeId.SHORT, "Quiero algo corto", "⏱️", 4, 240),
        MarathonTheme(MarathonThemeId.RAINY_DAY, "Día de lluvia", "🌧️", 3, 360),
        MarathonTheme(MarathonThemeId.SUNDAY_MODE, "Modo domingo", "☀️", 3, 400),
        MarathonTheme(MarathonThemeId.ADRENALINE, "Quiero adrenalina", "🔥", 3, 360),
        MarathonTheme(MarathonThemeId.ROMANCE, "Quiero romance", "💕", 2, 300),
        MarathonTheme(MarathonThemeId.PLOT_TWIST, "Quiero un plot twist", "🔄", 3, 360),
        MarathonTheme(MarathonThemeId.CEREBRAL, "Quiero algo cerebral", "🧠", 2, 300),
        MarathonTheme(MarathonThemeId.FRIENDS_NIGHT, "Noche con amigos", "🍿", 3, 400),
        MarathonTheme(MarathonThemeId.DISCOVER_NEW, "Quiero descubrir algo nuevo", "🌟", 3, 360),
    )

    override suspend fun generatePlan(themeId: MarathonThemeId): Result<MarathonPlan> {
        val theme = getThemes().find { it.id == themeId }
            ?: return Result.Error(UnknownError("Theme not found"))

        val candidates = fetchCandidatesForTheme(theme)
        if (candidates.isEmpty()) {
            return Result.Error(UnknownError("No movies found"))
        }

        val geminiResult = geminiAiService.generateMarathonPlan(
            theme = theme,
            candidates = candidates.map { it.toCandidate() },
            topGenres = getGenresForTheme(themeId),
            region = "AR",
        )

        return when (geminiResult) {
            is Result.Success -> {
                val aiResult = geminiResult.data
                val picksWithDetails = aiResult.picks.mapNotNull { aiPick ->
                    candidates.find { it.id == aiPick.movieId }?.let { movie ->
                        MarathonPick(
                            movieId = movie.id,
                            movieTitle = movie.title,
                            moviePoster = movie.poster,
                            movieYear = movie.releaseDate?.take(4),
                            movieRating = 0.0,
                            order = aiPick.order,
                            why = aiPick.why,
                            warnings = aiPick.warnings,
                        )
                    }
                }

                if (picksWithDetails.isEmpty()) {
                    Result.Success(generateFallbackPlan(theme, candidates))
                } else {
                    Result.Success(
                        MarathonPlan(
                            themeId = themeId,
                            themeTitle = theme.title,
                            tagline = aiResult.tagline,
                            picks = picksWithDetails,
                            generatedByAi = true,
                        )
                    )
                }
            }
            is Result.Error -> {
                Result.Success(generateFallbackPlan(theme, candidates))
            }
            is Result.Loading -> Result.Loading
        }
    }

    override suspend fun savePlan(plan: MarathonPlan): Long {
        return marathonLocalDataSource.saveMarathon(plan)
    }

    override fun getHistory(): Flow<List<MarathonHistoryItem>> {
        return marathonLocalDataSource.getMarathonHistory()
    }

    override suspend fun getMarathonById(marathonId: Long): MarathonPlan? {
        return marathonLocalDataSource.getMarathonById(marathonId)
    }

    private suspend fun fetchCandidatesForTheme(theme: MarathonTheme): List<Movie> {
        val genres = getGenresForTheme(theme.id)
        val result = movieRemoteDataSource.discoverMovies(
            genres = genres,
            sortBy = "popularity.desc",
            minVoteAverage = 6.0f,
            yearFrom = 1990,
            yearTo = 2025,
        )
        return when (result) {
            is Result.Success -> result.data
            else -> emptyList()
        }
    }

    private fun getGenresForTheme(themeId: MarathonThemeId): List<Int> {
        return when (themeId) {
            MarathonThemeId.LAUGH -> listOf(35)
            MarathonThemeId.CRY -> listOf(18, 10749)
            MarathonThemeId.SHORT -> listOf(35, 16)
            MarathonThemeId.RAINY_DAY -> listOf(18, 9648)
            MarathonThemeId.SUNDAY_MODE -> listOf(10751, 35)
            MarathonThemeId.ADRENALINE -> listOf(28, 53)
            MarathonThemeId.ROMANCE -> listOf(10749, 18)
            MarathonThemeId.PLOT_TWIST -> listOf(53, 9648, 878)
            MarathonThemeId.CEREBRAL -> listOf(878, 9648, 18)
            MarathonThemeId.FRIENDS_NIGHT -> listOf(28, 35, 12)
            MarathonThemeId.DISCOVER_NEW -> listOf(99, 36, 10752)
        }
    }

    private fun generateFallbackPlan(theme: MarathonTheme, candidates: List<Movie>): MarathonPlan {
        val selectedMovies = candidates
            .distinctBy { it.id }
            .take(theme.targetItems)

        val picks = selectedMovies.mapIndexed { index, movie ->
            MarathonPick(
                movieId = movie.id,
                movieTitle = movie.title,
                moviePoster = movie.poster,
                movieYear = movie.releaseDate.take(4),
                movieRating = 0.0,
                order = index + 1,
                why = "Encaja con ${theme.title} por su género y estilo",
                warnings = emptyList(),
            )
        }

        return MarathonPlan(
            themeId = theme.id,
            themeTitle = theme.title,
            tagline = "Tu maratón de ${theme.title} está lista ${theme.emoji}",
            picks = picks,
            generatedByAi = false,
        )
    }

    private fun Movie.toCandidate(): MarathonCandidate {
        return MarathonCandidate(
            id = id,
            title = title,
            overview = overview,
            genreIds = emptyList(),
            voteAverage = 0.0,
            popularity = 0.0,
            year = releaseDate.take(4),
            poster = poster,
        )
    }
}

