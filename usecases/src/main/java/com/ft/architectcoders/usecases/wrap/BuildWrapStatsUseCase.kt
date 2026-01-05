package com.ft.architectcoders.usecases.wrap

import com.ft.architectcoders.data.repository.aisearch.AiSearchRepository
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Profile
import com.ft.architectcoders.domain.model.TmdbGenres
import com.ft.architectcoders.domain.model.WrapAiSearchHighlights
import com.ft.architectcoders.domain.model.WrapGenreCount
import com.ft.architectcoders.domain.model.WrapMovieScore
import com.ft.architectcoders.domain.model.WrapPeriod
import com.ft.architectcoders.domain.model.WrapPersonCount
import com.ft.architectcoders.domain.model.WrapStats
import com.ft.architectcoders.domain.model.WrapTotals
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

interface BuildWrapStatsUseCase {
    suspend operator fun invoke(): WrapStatsResult
}

data class WrapStatsResult(
    val stats: WrapStats?,
    val profile: Profile,
    val isEmpty: Boolean,
)

class BuildWrapStatsUseCaseImpl(
    private val movieRepository: MovieRepository,
    private val profileRepository: ProfileRepository,
    private val aiSearchRepository: AiSearchRepository,
) : BuildWrapStatsUseCase {

    companion object {
        private const val MAX_CREDITS_FETCH = 20
    }

    override suspend fun invoke(): WrapStatsResult {
        val profile = profileRepository.profile.first()
        val period = buildPeriod()

        val allMovies = movieRepository.movies.first()

        val favorites = allMovies.filter { it.favorite }

        val aiSessions = try {
            aiSearchRepository.getAllSessions().firstOrNull() ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }

        val totalInteractions = allMovies.size + favorites.size + aiSessions.size
        if (totalInteractions == 0) {
            return WrapStatsResult(
                stats = null,
                profile = profile,
                isEmpty = true,
            )
        }

        val totals = WrapTotals(
            moviesViewed = allMovies.size,
            favorites = favorites.size,
            aiSearchSessions = aiSessions.size,
        )

        val topGenres = buildTopGenres(profile.favoriteGenres)

        val topMovies = buildTopMovies(allMovies, favorites)

        val favoritesHighlights = favorites.take(5).map { movie ->
            val year = movie.releaseDate.take(4).toIntOrNull() ?: 2020
            WrapMovieScore(
                id = movie.id,
                title = movie.title,
                year = year,
                poster = movie.poster,
                score = 100,
            )
        }

        val (topActors, topDirectors) = fetchCreditsForTopMovies(topMovies.take(MAX_CREDITS_FETCH))

        val aiSearchHighlights = buildAiSearchHighlights(aiSessions)

        val stats = WrapStats(
            period = period,
            totals = totals,
            topGenres = topGenres,
            topMovies = topMovies,
            favoritesHighlights = favoritesHighlights,
            topActors = topActors,
            topDirectors = topDirectors,
            aiSearchHighlights = aiSearchHighlights,
        )

        return WrapStatsResult(
            stats = stats,
            profile = profile,
            isEmpty = false,
        )
    }

    private fun buildPeriod(): WrapPeriod {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

        calendar.set(Calendar.DAY_OF_YEAR, 1)
        val fromDate = formatter.format(calendar.time)

        val toCalendar = Calendar.getInstance()
        val toDate = formatter.format(toCalendar.time)

        return WrapPeriod(
            fromDate = fromDate,
            toDate = toDate,
        )
    }

    private fun buildTopGenres(favoriteGenres: List<String>): List<WrapGenreCount> {
        if (favoriteGenres.isEmpty()) {
            return listOf(
                WrapGenreCount(TmdbGenres.DRAMA, TmdbGenres.nameForId(TmdbGenres.DRAMA)!!, 5),
                WrapGenreCount(TmdbGenres.ACTION, TmdbGenres.nameForId(TmdbGenres.ACTION)!!, 4),
                WrapGenreCount(TmdbGenres.COMEDY, TmdbGenres.nameForId(TmdbGenres.COMEDY)!!, 3),
            )
        }

        return favoriteGenres.mapIndexed { index, genreName ->
            val genreId = TmdbGenres.idForName(genreName) ?: (index + 1)

            WrapGenreCount(
                id = genreId,
                name = genreName,
                count = favoriteGenres.size - index,
            )
        }.take(5)
    }

    private fun buildTopMovies(
        allMovies: List<com.ft.architectcoders.domain.model.Movie>,
        favorites: List<com.ft.architectcoders.domain.model.Movie>,
    ): List<WrapMovieScore> {
        val favoriteIds = favorites.map { it.id }.toSet()

        return allMovies.map { movie ->
            val year = movie.releaseDate.take(4).toIntOrNull() ?: 2020
            val baseScore = if (movie.id in favoriteIds) 50 else 10
            val aiBonus = if (movie.aiRating != null) (movie.aiRating!! * 5).toInt() else 0

            WrapMovieScore(
                id = movie.id,
                title = movie.title,
                year = year,
                poster = movie.poster,
                score = baseScore + aiBonus,
            )
        }.sortedByDescending { it.score }.take(10)
    }

    private suspend fun fetchCreditsForTopMovies(
        topMovies: List<WrapMovieScore>,
    ): Pair<List<WrapPersonCount>, List<WrapPersonCount>> {
        val actorCounts = mutableMapOf<Int, Pair<Cast, Int>>()

        for (movie in topMovies.take(MAX_CREDITS_FETCH)) {
            try {
                val creditsResult = movieRepository.getMovieCredits(movie.id).first()
                if (creditsResult is Result.Success) {
                    creditsResult.data.take(3).forEach { cast ->
                        val current = actorCounts[cast.id]
                        if (current != null) {
                            actorCounts[cast.id] = cast to (current.second + 1)
                        } else {
                            actorCounts[cast.id] = cast to 1
                        }
                    }
                }
            } catch (e: Exception) {
                // TODO
            }
        }

        val topActors = actorCounts.values
            .sortedByDescending { it.second }
            .take(5)
            .map { (cast, count) ->
                WrapPersonCount(
                    id = cast.id,
                    name = cast.name,
                    profilePhoto = cast.profilePhoto,
                    count = count,
                )
            }

        val topDirectors = emptyList<WrapPersonCount>()

        return topActors to topDirectors
    }

    private fun buildAiSearchHighlights(
        sessions: List<com.ft.architectcoders.domain.model.AiSearchSession>,
    ): WrapAiSearchHighlights? {
        if (sessions.isEmpty()) return null

        val allKeywords = sessions.flatMap { session ->
            session.detectedPreferences?.keywords ?: emptyList()
        }

        val userMessages = sessions.flatMap { session ->
            session.messages.filter { it.role == com.ft.architectcoders.domain.model.ChatRole.USER }
                .map { it.content }
        }

        val topIntents = userMessages.take(5)
            .map { it.take(50) }
            .distinct()

        val topKeywords = allKeywords
            .groupingBy { it.lowercase() }
            .eachCount()
            .entries
            .sortedByDescending { it.value }
            .take(5)
            .map { it.key }

        if (topIntents.isEmpty() && topKeywords.isEmpty()) return null

        return WrapAiSearchHighlights(
            topIntents = topIntents,
            topKeywords = topKeywords,
            sessionCount = sessions.size,
        )
    }
}
