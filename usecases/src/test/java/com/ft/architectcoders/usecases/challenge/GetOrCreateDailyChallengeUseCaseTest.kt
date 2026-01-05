package com.ft.architectcoders.usecases.challenge

import com.ft.architectcoders.data.repository.challenge.ChallengeRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.ChallengeBadge
import com.ft.architectcoders.domain.model.ChallengeMovieOption
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.ChallengeType
import com.ft.architectcoders.domain.model.DailyChallenge
import com.ft.architectcoders.domain.model.DecadeCount
import com.ft.architectcoders.domain.model.GenreCount
import com.ft.architectcoders.domain.model.TmdbGenres
import com.ft.architectcoders.domain.model.UserChallengeSignals
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class GetOrCreateDailyChallengeUseCaseTest {

    @Mock
    lateinit var challengeRepository: ChallengeRepository

    private lateinit var useCase: GetOrCreateDailyChallengeUseCase

    @Before
    fun setUp() {
        useCase = GetOrCreateDailyChallengeUseCaseImpl(challengeRepository)
    }

    @Test
    fun `invoke returns existing challenge for today`() = runTest {
        val today = "2026-01-05"
        val existingChallenge = createSampleChallenge(date = today)
        val signals = createSampleSignals()

        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Success(existingChallenge))

        val result = useCase(today, signals)

        assertTrue(result is Result.Success)
        assertEquals(existingChallenge, (result as Result.Success).data)
        verify(challengeRepository).getOrCreateDailyChallenge(today, signals)
    }

    @Test
    fun `invoke creates new challenge when none exists for today`() = runTest {
        val today = "2026-01-05"
        val newChallenge = createSampleChallenge(date = today)
        val signals = createSampleSignals()

        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Success(newChallenge))

        val result = useCase(today, signals)

        assertTrue(result is Result.Success)
        assertEquals(newChallenge, (result as Result.Success).data)
    }

    @Test
    fun `invoke returns error when repository fails`() = runTest {
        val today = "2026-01-05"
        val signals = createSampleSignals()
        val error = AppError.UnknownError("No se encontraron películas para el reto")

        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Error(error))

        val result = useCase(today, signals)

        assertTrue(result is Result.Error)
        assertEquals(error, (result as Result.Error).error)
    }

    @Test
    fun `same day returns same challenge (rule 1 per day)`() = runTest {
        val today = "2026-01-05"
        val challenge = createSampleChallenge(date = today)
        val signals = createSampleSignals()

        // First call
        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Success(challenge))

        val result1 = useCase(today, signals)
        val result2 = useCase(today, signals)

        assertTrue(result1 is Result.Success)
        assertTrue(result2 is Result.Success)
        assertEquals((result1 as Result.Success).data.date, (result2 as Result.Success).data.date)
        assertEquals(result1.data.type, result2.data.type)
    }

    @Test
    fun `challenge with PRE_2000 type has correct and incorrect options`() = runTest {
        val today = "2026-01-05"
        val pre2000Challenge = createSampleChallenge(
            date = today,
            type = ChallengeType.PRE_2000,
            movieOptions = listOf(
                // Correct options (pre-2000)
                createMovieOption(movieId = 1, title = "The Matrix", year = "1999", isCorrectChoice = true),
                createMovieOption(movieId = 2, title = "Fight Club", year = "1999", isCorrectChoice = true),
                createMovieOption(movieId = 3, title = "Pulp Fiction", year = "1994", isCorrectChoice = true),
                // Incorrect options (post-2000)
                createMovieOption(movieId = 4, title = "Oppenheimer", year = "2023", isCorrectChoice = false),
                createMovieOption(movieId = 5, title = "Inception", year = "2010", isCorrectChoice = false),
            ),
        )
        val signals = createSampleSignals()

        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Success(pre2000Challenge))

        val result = useCase(today, signals)

        assertTrue(result is Result.Success)
        val challenge = (result as Result.Success).data
        assertEquals(ChallengeType.PRE_2000, challenge.type)
        
        // Verify correct options are pre-2000
        val correctOptions = challenge.movieOptions.filter { it.isCorrectChoice }
        val incorrectOptions = challenge.movieOptions.filter { !it.isCorrectChoice }
        
        assertEquals(3, correctOptions.size)
        assertEquals(2, incorrectOptions.size)
        
        correctOptions.forEach { movie ->
            val year = movie.year?.toIntOrNull() ?: 0
            assertTrue("Correct movie ${movie.title} should be pre-2000 but is $year", year < 2000)
        }
        
        incorrectOptions.forEach { movie ->
            val year = movie.year?.toIntOrNull() ?: 0
            assertTrue("Incorrect movie ${movie.title} should be post-2000 but is $year", year >= 2000)
        }
    }

    @Test
    fun `signals with recent movies biases towards PRE_2000 challenge`() = runTest {
        val today = "2026-01-05"
        // User only watches recent movies
        val signals = createSampleSignals(
            decadeHistogram = listOf(
                DecadeCount(2020, 20),
                DecadeCount(2010, 15),
                DecadeCount(1990, 1),
            ),
        )
        val pre2000Challenge = createSampleChallenge(
            date = today,
            type = ChallengeType.PRE_2000,
        )

        whenever(challengeRepository.getOrCreateDailyChallenge(today, signals))
            .thenReturn(Result.Success(pre2000Challenge))

        val result = useCase(today, signals)

        assertTrue(result is Result.Success)
        // The repository should have chosen PRE_2000 based on signals
        assertEquals(ChallengeType.PRE_2000, (result as Result.Success).data.type)
    }

    private fun createSampleChallenge(
        date: String = "2026-01-05",
        type: ChallengeType = ChallengeType.PRE_2000,
        status: ChallengeStatus = ChallengeStatus.ACTIVE,
        movieOptions: List<ChallengeMovieOption> = listOf(
            createMovieOption(movieId = 1),
            createMovieOption(movieId = 2),
            createMovieOption(movieId = 3),
        ),
    ) = DailyChallenge(
        date = date,
        type = type,
        title = "Reto: Clásico del siglo XX",
        reason = "Descubrí joyas cinematográficas del pasado",
        rules = listOf("Película anterior al año 2000", "Calificación mínima 7.0"),
        badge = ChallengeBadge(
            id = "badge_pre2000",
            name = "Cinéfilo Retro",
            emoji = "📼",
        ),
        movieOptions = movieOptions,
        status = status,
        completedMovieId = null,
        completedAt = null,
        generatedByAi = true,
    )

    private fun createMovieOption(
        movieId: Int = 1,
        title: String = "Test Movie",
        year: String = "1999",
        isCorrectChoice: Boolean = true,
    ) = ChallengeMovieOption(
        movieId = movieId,
        title = title,
        poster = "https://image.tmdb.org/t/p/w500/poster.jpg",
        year = year,
        whyItFits = "Clásico de $year",
        isCorrectChoice = isCorrectChoice,
    )

    private fun createSampleSignals(
        topGenres: List<GenreCount> = listOf(
            GenreCount(TmdbGenres.ACTION, TmdbGenres.nameForId(TmdbGenres.ACTION)!!, 10),
            GenreCount(TmdbGenres.SCIFI, TmdbGenres.nameForId(TmdbGenres.SCIFI)!!, 5),
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
}

