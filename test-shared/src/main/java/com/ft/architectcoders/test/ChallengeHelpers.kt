package com.ft.architectcoders.test

import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.ChallengeType
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.DecadeCount
import com.ft.architectcoders.domain.model.GenreCount
import com.ft.architectcoders.domain.model.UserChallengeSignals

fun sampleChallengeBadge(
    id: String = "badge_pre2000",
    name: String = "Cinéfilo Retro",
    emoji: String = "📼",
    unlockedAt: Long? = null,
) = ChallengeBadge(
    id = id,
    name = name,
    emoji = emoji,
    unlockedAt = unlockedAt,
)

fun sampleChallengeMovieOption(
    movieId: Int = 1,
    title: String = "The Matrix",
    poster: String? = "https://image.tmdb.org/t/p/w500/poster.jpg",
    year: String? = "1999",
    whyItFits: String = "Clásico de 1999",
    isCorrectChoice: Boolean = true,
) = ChallengeMovieOption(
    movieId = movieId,
    title = title,
    poster = poster,
    year = year,
    whyItFits = whyItFits,
    isCorrectChoice = isCorrectChoice,
)

fun sampleDailyChallenge(
    date: String = "2026-01-05",
    type: ChallengeType = ChallengeType.PRE_2000,
    title: String = "Reto: Clásico del siglo XX",
    reason: String = "Descubrí joyas cinematográficas del pasado",
    rules: List<String> = listOf("Película anterior al año 2000", "Calificación mínima 7.0"),
    badge: ChallengeBadge = sampleChallengeBadge(),
    movieOptions: List<ChallengeMovieOption> = listOf(
        // 3 correct movies (pre-2000)
        sampleChallengeMovieOption(movieId = 1, title = "The Matrix", year = "1999", isCorrectChoice = true),
        sampleChallengeMovieOption(movieId = 2, title = "Fight Club", year = "1999", isCorrectChoice = true),
        sampleChallengeMovieOption(movieId = 3, title = "Pulp Fiction", year = "1994", isCorrectChoice = true),
        // 2 incorrect movies (post-2000)
        sampleChallengeMovieOption(movieId = 4, title = "Oppenheimer", year = "2023", isCorrectChoice = false, whyItFits = "¿Será anterior al 2000?"),
        sampleChallengeMovieOption(movieId = 5, title = "Inception", year = "2010", isCorrectChoice = false, whyItFits = "¿Será anterior al 2000?"),
    ),
    status: ChallengeStatus = ChallengeStatus.ACTIVE,
    completedMovieId: Int? = null,
    completedAt: Long? = null,
    generatedByAi: Boolean = true,
) = DailyChallenge(
    date = date,
    type = type,
    title = title,
    reason = reason,
    rules = rules,
    badge = badge,
    movieOptions = movieOptions,
    status = status,
    completedMovieId = completedMovieId,
    completedAt = completedAt,
    generatedByAi = generatedByAi,
)

fun sampleUserChallengeSignals(
    topGenres: List<GenreCount> = listOf(
        GenreCount(28, "Action", 10),
        GenreCount(878, "Sci-Fi", 5),
    ),
    decadeHistogram: List<DecadeCount> = listOf(
        DecadeCount(2020, 15),
        DecadeCount(2010, 10),
        DecadeCount(1990, 2),
    ),
    favoriteMovieIds: List<Int> = listOf(1, 2, 3),
) = UserChallengeSignals(
    topGenres = topGenres,
    decadeHistogram = decadeHistogram,
    favoriteMovieIds = favoriteMovieIds,
)

