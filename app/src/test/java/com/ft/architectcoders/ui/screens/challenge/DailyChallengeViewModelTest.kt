package com.ft.architectcoders.ui.screens.challenge

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.ChallengeStatus
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.UserChallengeSignals
import com.ft.architectcoders.test.sampleDailyChallenge
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.challenge.CompleteDailyChallengeUseCase
import com.ft.architectcoders.usecases.challenge.GetOrCreateDailyChallengeUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class DailyChallengeViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getOrCreateDailyChallengeUseCase: GetOrCreateDailyChallengeUseCase

    @Mock
    lateinit var completeDailyChallengeUseCase: CompleteDailyChallengeUseCase

    @Mock
    lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @Test
    fun `Initial load shows Loading then Success`() = runTest {
        val challenge = sampleDailyChallenge()
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(challenge))

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Success, got $finalState",
                finalState is DailyChallengeUiState.Success,
            )
            val successState = finalState as DailyChallengeUiState.Success
            assertEquals(challenge.title, successState.challenge.title)
            assertEquals(5, successState.challenge.movieOptions.size) // 3 correct + 2 incorrect
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Generate error shows Error state`() = runTest {
        val errorMessage = "No se encontraron películas para el reto"
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(
            Result.Error(AppError.UnknownError(message = errorMessage)),
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Error, got $finalState",
                finalState is DailyChallengeUiState.Error,
            )
            assertEquals(errorMessage, (finalState as DailyChallengeUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Existing challenge for today returns same challenge without regenerating`() = runTest {
        val existingChallenge = sampleDailyChallenge()
        var callCount = 0

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenAnswer {
            callCount++
            Result.Success(existingChallenge)
        }

        val viewModel = createViewModel()

        viewModel.state.test {
            val state1 = expectMostRecentItem()
            assertTrue(state1 is DailyChallengeUiState.Success)

            // The use case should only be called once for today's challenge
            assertEquals(1, callCount)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Complete challenge updates status to COMPLETED`() = runTest {
        val challenge = sampleDailyChallenge()
        val completedChallenge = challenge.copy(
            status = ChallengeStatus.COMPLETED,
            completedMovieId = 1,
            completedAt = System.currentTimeMillis(),
        )

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(challenge))
        whenever(completeDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(completedChallenge))

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.ACTIVE, (initialState as DailyChallengeUiState.Success).challenge.status)

            viewModel.onCompleteChallenge(challenge.movieOptions.first())

            val completedState = expectMostRecentItem()
            assertTrue(completedState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.COMPLETED, (completedState as DailyChallengeUiState.Success).challenge.status)
            assertTrue(completedState.showCompletionCelebration)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Already completed challenge cannot be completed again`() = runTest {
        val completedChallenge = sampleDailyChallenge(
            status = ChallengeStatus.COMPLETED,
            completedMovieId = 1,
            completedAt = System.currentTimeMillis(),
        )

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(completedChallenge))

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.COMPLETED, (initialState as DailyChallengeUiState.Success).challenge.status)

            // Try to complete again - this should be ignored (no new emission)
            viewModel.onCompleteChallenge(completedChallenge.movieOptions.first())

            // Give time for any potential emission
            advanceUntilIdle()

            // Verify state hasn't changed by checking current value
            val currentState = viewModel.state.value
            assertTrue(currentState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.COMPLETED, (currentState as DailyChallengeUiState.Success).challenge.status)
            assertFalse(currentState.isCompleting)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Choosing incorrect movie fails the challenge`() = runTest {
        val challenge = sampleDailyChallenge()
        // Get the incorrect movie (the 4th one in our sample)
        val incorrectMovie = challenge.movieOptions.first { !it.isCorrectChoice }
        val failedChallenge = challenge.copy(
            status = ChallengeStatus.FAILED,
            completedMovieId = incorrectMovie.movieId,
            completedAt = System.currentTimeMillis(),
        )

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(challenge))
        whenever(completeDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(failedChallenge))

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.ACTIVE, (initialState as DailyChallengeUiState.Success).challenge.status)

            viewModel.onCompleteChallenge(incorrectMovie)

            val failedState = expectMostRecentItem()
            assertTrue(failedState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.FAILED, (failedState as DailyChallengeUiState.Success).challenge.status)
            assertFalse(failedState.showCompletionCelebration) // No celebration for failure

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Failed challenge cannot be retried`() = runTest {
        val failedChallenge = sampleDailyChallenge(
            status = ChallengeStatus.FAILED,
            completedMovieId = 4, // An incorrect movie
            completedAt = System.currentTimeMillis(),
        )

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(failedChallenge))

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.FAILED, (initialState as DailyChallengeUiState.Success).challenge.status)

            // Try to complete again - should be ignored (no new emission)
            viewModel.onCompleteChallenge(failedChallenge.movieOptions.first { it.isCorrectChoice })

            // Give time for any potential emission
            advanceUntilIdle()

            // Verify state hasn't changed by checking current value
            val currentState = viewModel.state.value
            assertTrue(currentState is DailyChallengeUiState.Success)
            assertEquals(ChallengeStatus.FAILED, (currentState as DailyChallengeUiState.Success).challenge.status)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `ChallengeFailed event contains correct movie titles`() = runTest {
        val challenge = sampleDailyChallenge()
        val incorrectMovie = challenge.movieOptions.first { !it.isCorrectChoice }
        val failedChallenge = challenge.copy(
            status = ChallengeStatus.FAILED,
            completedMovieId = incorrectMovie.movieId,
            completedAt = System.currentTimeMillis(),
        )

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(challenge))
        whenever(completeDailyChallengeUseCase(any(), any())).thenReturn(Result.Success(failedChallenge))

        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onCompleteChallenge(incorrectMovie)

            val event = awaitItem()
            assertTrue(event is DailyChallengeEvent.ChallengeFailed)
            val failedEvent = event as DailyChallengeEvent.ChallengeFailed
            
            // Should contain the correct movie titles
            assertTrue(failedEvent.correctMovies.contains("The Matrix"))
            assertTrue(failedEvent.correctMovies.contains("Fight Club"))
            assertTrue(failedEvent.correctMovies.contains("Pulp Fiction"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetry reloads challenge`() = runTest {
        val challenge1 = sampleDailyChallenge(title = "Challenge 1")
        val challenge2 = sampleDailyChallenge(title = "Challenge 2")
        var callCount = 0

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenAnswer {
            callCount++
            if (callCount == 1) Result.Success(challenge1) else Result.Success(challenge2)
        }

        val viewModel = createViewModel()

        viewModel.state.test {
            val state1 = expectMostRecentItem()
            assertTrue(state1 is DailyChallengeUiState.Success)
            assertEquals("Challenge 1", (state1 as DailyChallengeUiState.Success).challenge.title)

            viewModel.onRetry()

            val state2 = expectMostRecentItem()
            assertTrue(state2 is DailyChallengeUiState.Success)
            // Note: In real scenario, if it's the same day, it would return the same challenge
            // This test verifies the retry mechanism works
            assertEquals("Challenge 2", (state2 as DailyChallengeUiState.Success).challenge.title)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `User signals are built from favorite movies`() = runTest {
        val favoriteMovies = listOf(
            createMovie(id = 1, title = "Movie 1", releaseDate = "2020-01-01", favorite = true),
            createMovie(id = 2, title = "Movie 2", releaseDate = "2015-06-15", favorite = true),
            createMovie(id = 3, title = "Movie 3", releaseDate = "1995-03-20", favorite = true),
        )
        val allMovies = favoriteMovies + listOf(
            createMovie(id = 4, title = "Movie 4", releaseDate = "2022-01-01", favorite = false),
        )

        val challenge = sampleDailyChallenge()

        var capturedSignals: UserChallengeSignals? = null

        whenever(fetchMoviesUseCase()).thenReturn(flowOf(allMovies))
        whenever(getOrCreateDailyChallengeUseCase(any(), any())).thenAnswer { invocation ->
            capturedSignals = invocation.getArgument(1)
            Result.Success(challenge)
        }

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            // Verify signals were built correctly
            assertTrue(capturedSignals != null)
            assertEquals(3, capturedSignals?.favoriteMovieIds?.size)
            assertTrue(capturedSignals?.favoriteMovieIds?.contains(1) == true)
            assertTrue(capturedSignals?.favoriteMovieIds?.contains(2) == true)
            assertTrue(capturedSignals?.favoriteMovieIds?.contains(3) == true)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = DailyChallengeViewModel(
        getOrCreateDailyChallengeUseCase = getOrCreateDailyChallengeUseCase,
        completeDailyChallengeUseCase = completeDailyChallengeUseCase,
        fetchMoviesUseCase = fetchMoviesUseCase,
    )

    private fun createMovie(
        id: Int,
        title: String,
        releaseDate: String,
        favorite: Boolean,
    ) = Movie(
        id = id,
        title = title,
        originalTitle = title,
        poster = "",
        backdrop = null,
        releaseDate = releaseDate,
        overview = "",
        favorite = favorite,
        aiRating = null,
        aiQuote = null,
    )
}

