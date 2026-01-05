package com.ft.architectcoders.data.repository.mood

import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.GenreWeight
import com.ft.architectcoders.domain.model.MoodProfile
import com.ft.architectcoders.domain.model.MoodVector
import com.ft.architectcoders.domain.model.Movie

class MoodRepositoryImpl(
    private val geminiAiService: GeminiAiService,
    private val movieRemoteDataSource: MovieRemoteDataSource,
) : MoodRepository {
    override suspend fun buildMoodProfile(moodVector: MoodVector): Result<MoodProfile> {
        return when (val result = geminiAiService.generateMoodProfile(moodVector)) {
            is Result.Success -> result
            is Result.Error -> {
                Result.Success(generateFallbackProfile(moodVector))
            }
            is Result.Loading -> result
        }
    }

    override suspend fun getRecommendations(profile: MoodProfile): Result<List<Movie>> {
        val genreIds = profile.genres.map { it.id }

        return movieRemoteDataSource.discoverMovies(
            genres = genreIds.ifEmpty { null },
            excludeGenres = profile.excludeGenres.ifEmpty { null },
            sortBy = profile.sortBy,
            minVoteAverage = profile.minVoteAverage,
            yearFrom = profile.yearFrom,
            yearTo = profile.yearTo,
        )
    }

    private fun generateFallbackProfile(moodVector: MoodVector): MoodProfile {
        val genres = mutableListOf<GenreWeight>()
        val explanations = mutableListOf<String>()
        val excludeGenres = mutableListOf<Int>()

        if (moodVector.energy > 60) {
            genres.add(GenreWeight(GENRE_ACTION, 0.8f))
            genres.add(GenreWeight(GENRE_ADVENTURE, 0.6f))
            explanations.add("Películas con mucha acción y aventura")
        }

        if (moodVector.humor > 60) {
            genres.add(GenreWeight(GENRE_COMEDY, 0.9f))
            explanations.add("Comedias para reír sin parar")
            excludeGenres.add(GENRE_HORROR)
        }

        if (moodVector.tension > 60) {
            genres.add(GenreWeight(GENRE_THRILLER, 0.8f))
            genres.add(GenreWeight(GENRE_CRIME, 0.6f))
            genres.add(GenreWeight(GENRE_MYSTERY, 0.5f))
            explanations.add("Thrillers intensos con suspenso")
        }

        if (moodVector.romance > 60) {
            genres.add(GenreWeight(GENRE_ROMANCE, 0.9f))
            genres.add(GenreWeight(GENRE_DRAMA, 0.5f))
            explanations.add("Historias románticas que tocan el corazón")
        }

        if (moodVector.cerebral > 60) {
            genres.add(GenreWeight(GENRE_SCIFI, 0.7f))
            genres.add(GenreWeight(GENRE_MYSTERY, 0.6f))
            explanations.add("Películas que hacen pensar")
        }

        if (genres.isEmpty()) {
            genres.add(GenreWeight(GENRE_DRAMA, 0.5f))
            genres.add(GenreWeight(GENRE_COMEDY, 0.5f))
            explanations.add("Una mezcla variada para tu noche")
        }

        val microCopy = buildFallbackMicroCopy(moodVector)

        return MoodProfile(
            microCopy = microCopy,
            genres = genres.distinctBy { it.id },
            excludeGenres = excludeGenres.distinct(),
            globalExplanation = explanations.take(3),
            sortBy = "popularity.desc",
            minVoteAverage = 6.0f,
            yearFrom = 2000,
            yearTo = 2025,
            generatedByAi = false,
        )
    }

    private fun buildFallbackMicroCopy(moodVector: MoodVector): String {
        val dominant =
            listOf(
                "energía" to moodVector.energy,
                "risas" to moodVector.humor,
                "tensión" to moodVector.tension,
                "romance" to moodVector.romance,
                "reflexión" to moodVector.cerebral,
            ).maxByOrNull { it.second }

        return when (dominant?.first) {
            "energía" -> "Hoy estás para películas con adrenalina pura"
            "risas" -> "Tu mood pide comedias y buenas risas"
            "tensión" -> "Listo para thrillers que te mantengan al borde"
            "romance" -> "El amor está en el aire esta noche"
            "reflexión" -> "Películas para pensar y disfrutar"
            else -> "Una noche de cine perfecta te espera"
        }
    }

    companion object {
        private const val GENRE_ACTION = 28
        private const val GENRE_ADVENTURE = 12
        private const val GENRE_COMEDY = 35
        private const val GENRE_CRIME = 80
        private const val GENRE_DRAMA = 18
        private const val GENRE_HORROR = 27
        private const val GENRE_MYSTERY = 9648
        private const val GENRE_ROMANCE = 10749
        private const val GENRE_SCIFI = 878
        private const val GENRE_THRILLER = 53
    }
}
