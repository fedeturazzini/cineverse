package com.ft.architectcoders.data.repository.challenge

import com.ft.architectcoders.data.datasource.ChallengeAiResult
import com.ft.architectcoders.data.datasource.ChallengeFilters
import com.ft.architectcoders.data.datasource.ChallengeLocalDataSource
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.ChallengeType
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.UserChallengeSignals
import kotlinx.coroutines.flow.Flow

class ChallengeRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val movieRemoteDataSource: MovieRemoteDataSource,
    private val challengeLocalDataSource: ChallengeLocalDataSource,
) : ChallengeRepository {

    override suspend fun getOrCreateDailyChallenge(
        date: String,
        signals: UserChallengeSignals,
    ): Result<DailyChallenge> {
        val existingChallenge = challengeLocalDataSource.getChallengeByDate(date)
        if (existingChallenge != null) {
            return Result.Success(existingChallenge)
        }

        val geminiResult = geminiAiService.generateDailyChallenge(
            date = date,
            topGenres = signals.topGenres.map { it.id to it.count },
            decadeHistogram = signals.decadeHistogram.map { it.decade to it.count },
            favoriteMovieIds = signals.favoriteMovieIds,
        )

        val challengeSpec = when (geminiResult) {
            is Result.Success -> geminiResult.data
            is Result.Error -> generateFallbackChallenge(signals)
            is Result.Loading -> return Result.Loading
        }

        val correctMovies = fetchMoviesForChallenge(challengeSpec.filters, signals.favoriteMovieIds)
        if (correctMovies.isEmpty()) {
            return Result.Error(AppError.UnknownError("No se encontraron películas para el reto"))
        }

        val incorrectMovies = fetchIncorrectMovies(
            challengeSpec.type,
            challengeSpec.filters,
            signals.favoriteMovieIds + correctMovies.map { it.id },
        )

        val correctOptions = correctMovies.take(3).map { movie ->
            ChallengeMovieOption(
                movieId = movie.id,
                title = movie.title,
                poster = movie.poster,
                year = movie.releaseDate.take(4),
                whyItFits = generateWhyItFits(challengeSpec.type, movie),
                isCorrectChoice = true,
            )
        }

        val incorrectOptions = incorrectMovies.take(2).map { movie ->
            ChallengeMovieOption(
                movieId = movie.id,
                title = movie.title,
                poster = movie.poster,
                year = movie.releaseDate.take(4),
                whyItFits = generateIncorrectReason(challengeSpec.type),
                isCorrectChoice = false,
            )
        }

        val movieOptions = (correctOptions + incorrectOptions).shuffled()

        val challenge = DailyChallenge(
            date = date,
            type = challengeSpec.type,
            title = challengeSpec.title,
            reason = challengeSpec.reason,
            rules = challengeSpec.rules,
            badge = ChallengeBadge(
                id = challengeSpec.badgeId,
                name = challengeSpec.badgeName,
                emoji = challengeSpec.badgeEmoji,
            ),
            movieOptions = movieOptions,
            status = ChallengeStatus.ACTIVE,
            generatedByAi = geminiResult is Result.Success,
        )

        challengeLocalDataSource.saveChallenge(challenge)

        return Result.Success(challenge)
    }

    override suspend fun completeChallenge(
        date: String,
        movieId: Int,
    ): Result<DailyChallenge> {
        val challenge = challengeLocalDataSource.getChallengeByDate(date)
            ?: return Result.Error(AppError.UnknownError("Reto no encontrado"))

        if (challenge.status != ChallengeStatus.ACTIVE) {
            return Result.Success(challenge)
        }

        val selectedOption = challenge.movieOptions.find { it.movieId == movieId }
        val isCorrectChoice = selectedOption?.isCorrectChoice ?: false

        if (isCorrectChoice) {
            challengeLocalDataSource.completeChallenge(date, movieId)
        } else {
            challengeLocalDataSource.failChallenge(date, movieId)
        }

        val updatedChallenge = challengeLocalDataSource.getChallengeByDate(date)
            ?: return Result.Error(AppError.UnknownError("Error al actualizar el reto"))

        return Result.Success(updatedChallenge)
    }

    override fun getChallengeHistory(): Flow<List<DailyChallenge>> {
        return challengeLocalDataSource.getChallengeHistory()
    }

    override fun getUnlockedBadges(): Flow<List<ChallengeBadge>> {
        return challengeLocalDataSource.getUnlockedBadges()
    }

    private suspend fun fetchMoviesForChallenge(
        filters: ChallengeFilters,
        excludeIds: List<Int>,
    ): List<Movie> {

        var result = movieRemoteDataSource.discoverMovies(
            genres = filters.includeGenreIds,
            excludeGenres = filters.excludeGenreIds,
            sortBy = "popularity.desc",
            minVoteAverage = filters.minVoteAverage,
            yearFrom = filters.yearFrom,
            yearTo = filters.yearTo,
        )

        var movies = extractAndFilterMovies(result, excludeIds, filters)

        if (movies.isEmpty() && filters.minVoteAverage != null) {
            result = movieRemoteDataSource.discoverMovies(
                genres = filters.includeGenreIds,
                excludeGenres = filters.excludeGenreIds,
                sortBy = "popularity.desc",
                minVoteAverage = null,
                yearFrom = filters.yearFrom,
                yearTo = filters.yearTo,
            )
            movies = extractAndFilterMovies(result, excludeIds, filters)
        }

        if (movies.isEmpty() && filters.yearTo != null) {
            result = movieRemoteDataSource.discoverMovies(
                genres = null,
                excludeGenres = null,
                sortBy = "popularity.desc",
                minVoteAverage = 6.0f,
                yearFrom = filters.yearFrom,
                yearTo = filters.yearTo,
            )
            movies = extractAndFilterMovies(result, excludeIds, filters)
        }

        if (movies.isEmpty()) {
            result = movieRemoteDataSource.discoverMovies(
                genres = null,
                excludeGenres = null,
                sortBy = "popularity.desc",
                minVoteAverage = 6.0f,
                yearFrom = null,
                yearTo = null,
            )
            movies = extractAndFilterMovies(result, excludeIds, filters)
        }

        return movies
    }

    private fun extractAndFilterMovies(
        result: Result<List<Movie>>,
        excludeIds: List<Int>,
        filters: ChallengeFilters,
    ): List<Movie> {
        return when (result) {
            is Result.Success -> result.data
                .filter { it.id !in excludeIds }
                .filter { !isLikelySequel(it.title) || filters.yearTo != null }
                .take(10)
            else -> emptyList()
        }
    }

    private fun isLikelySequel(title: String): Boolean {
        val sequelPatterns = listOf(
            Regex("\\s+[2-9]$"),
            Regex("\\s+II[I]?$"),
            Regex("\\s+IV$"),
            Regex("\\s+V$"),
            Regex("\\bPart\\s+\\d"),
            Regex("\\bChapter\\s+\\d"),
            Regex("\\bCapítulo\\s+\\d"),
            Regex("\\bParte\\s+\\d"),
        )
        return sequelPatterns.any { it.containsMatchIn(title) }
    }

    private fun generateWhyItFits(type: ChallengeType, movie: Movie): String {
        return when (type) {
            ChallengeType.NEW_GENRE -> "Un género diferente a lo que solés ver"
            ChallengeType.PRE_2000 -> "Clásico de ${movie.releaseDate.take(4)}"
            ChallengeType.NO_SEQUELS_WEEK -> "Historia original y autónoma"
            ChallengeType.CLASSIC_AWARD_WINNER -> "Aclamada por la crítica"
        }
    }

    private fun generateIncorrectReason(type: ChallengeType): String {
        return when (type) {
            ChallengeType.NEW_GENRE -> "¿Será del género correcto?"
            ChallengeType.PRE_2000 -> "¿Será anterior al 2000?"
            ChallengeType.NO_SEQUELS_WEEK -> "¿Será una historia original?"
            ChallengeType.CLASSIC_AWARD_WINNER -> "¿Será un clásico premiado?"
        }
    }

    private suspend fun fetchIncorrectMovies(
        type: ChallengeType,
        correctFilters: ChallengeFilters,
        excludeIds: List<Int>,
    ): List<Movie> {
        val incorrectFilters = when (type) {
            ChallengeType.PRE_2000 -> {
                ChallengeFilters(yearFrom = 2000, minVoteAverage = 6.0f)
            }
            ChallengeType.NEW_GENRE -> {
                ChallengeFilters(
                    includeGenreIds = correctFilters.excludeGenreIds,
                    minVoteAverage = 6.0f,
                )
            }
            ChallengeType.NO_SEQUELS_WEEK -> {
                ChallengeFilters(minVoteAverage = 6.0f)
            }
            ChallengeType.CLASSIC_AWARD_WINNER -> {
                ChallengeFilters(yearFrom = 2020, minVoteAverage = 5.0f)
            }
        }

        val result = movieRemoteDataSource.discoverMovies(
            genres = incorrectFilters.includeGenreIds,
            excludeGenres = incorrectFilters.excludeGenreIds,
            sortBy = "popularity.desc",
            minVoteAverage = incorrectFilters.minVoteAverage,
            yearFrom = incorrectFilters.yearFrom,
            yearTo = incorrectFilters.yearTo,
        )

        var movies = when (result) {
            is Result.Success -> result.data.filter { it.id !in excludeIds }.take(5)
            else -> emptyList()
        }


        if (type == ChallengeType.NO_SEQUELS_WEEK) {
            movies = movies.filter { isLikelySequel(it.title) }
        }

        return movies
    }

    private fun generateFallbackChallenge(signals: UserChallengeSignals): ChallengeAiResult {
        return when (determineFallbackType(signals)) {
            ChallengeType.PRE_2000 -> ChallengeAiResult(
                type = ChallengeType.PRE_2000,
                title = "Reto: Clásico del siglo XX",
                reason = "Descubrí joyas cinematográficas del pasado",
                rules = listOf("Película anterior al año 2000", "Calificación mínima 7.0"),
                badgeId = "badge_pre2000",
                badgeName = "Cinéfilo Retro",
                badgeEmoji = "📼",
                filters = ChallengeFilters(
                    yearTo = 1999,
                    minVoteAverage = 7.0f,
                ),
                cardSubtitle = "Viajá al pasado del cine",
                completionCopy = "¡Viajaste en el tiempo cinematográfico!",
            )
            ChallengeType.NEW_GENRE -> ChallengeAiResult(
                type = ChallengeType.NEW_GENRE,
                title = "Reto: Género nuevo",
                reason = "Salí de tu zona de confort cinematográfica",
                rules = listOf("Género que no hayas explorado antes"),
                badgeId = "badge_new_genre",
                badgeName = "Explorador de Géneros",
                badgeEmoji = "🎭",
                filters = ChallengeFilters(
                    excludeGenreIds = signals.topGenres.take(3).map { it.id },
                    minVoteAverage = 6.5f,
                ),
                cardSubtitle = "Explorá nuevos horizontes",
                completionCopy = "¡Expandiste tus gustos cinematográficos!",
            )
            ChallengeType.NO_SEQUELS_WEEK -> ChallengeAiResult(
                type = ChallengeType.NO_SEQUELS_WEEK,
                title = "Reto: Sin secuelas",
                reason = "Historias originales y únicas",
                rules = listOf("Sin secuelas ni sagas", "Películas originales"),
                badgeId = "badge_no_sequels",
                badgeName = "Purista Original",
                badgeEmoji = "✨",
                filters = ChallengeFilters(
                    minVoteAverage = 7.0f,
                    yearFrom = 2010,
                ),
                cardSubtitle = "Solo historias originales",
                completionCopy = "¡Disfrutaste una historia única!",
            )
            ChallengeType.CLASSIC_AWARD_WINNER -> ChallengeAiResult(
                type = ChallengeType.CLASSIC_AWARD_WINNER,
                title = "Reto: Clásico premiado",
                reason = "Las mejores películas de la historia",
                rules = listOf("Calificación mínima 8.0", "Clásico reconocido"),
                badgeId = "badge_classic",
                badgeName = "Conocedor de Clásicos",
                badgeEmoji = "🏆",
                filters = ChallengeFilters(
                    yearTo = 2010,
                    minVoteAverage = 8.0f,
                ),
                cardSubtitle = "Lo mejor del cine",
                completionCopy = "¡Viste una obra maestra!",
            )
        }
    }

    private fun determineFallbackType(signals: UserChallengeSignals): ChallengeType {
        val recentDecades = signals.decadeHistogram
            .filter { it.decade >= 2000 }
            .sumOf { it.count }
        val oldDecades = signals.decadeHistogram
            .filter { it.decade < 2000 }
            .sumOf { it.count }

        return when {
            recentDecades > oldDecades * 3 -> ChallengeType.PRE_2000
            signals.topGenres.size <= 2 -> ChallengeType.NEW_GENRE
            else -> ChallengeType.CLASSIC_AWARD_WINNER
        }
    }
}

