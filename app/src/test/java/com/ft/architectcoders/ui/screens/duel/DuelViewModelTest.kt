package com.ft.architectcoders.ui.screens.duel

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.test.sampleDuelChoice
import com.ft.architectcoders.test.sampleDuelSession
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.sampleTasteFingerprint
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.duel.CompleteDuelSessionUseCase
import com.ft.architectcoders.usecases.duel.GetDuelCandidatesUseCase
import com.ft.architectcoders.usecases.duel.SubmitDuelChoiceUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
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
class DuelViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getDuelCandidatesUseCase: GetDuelCandidatesUseCase

    @Mock
    lateinit var submitDuelChoiceUseCase: SubmitDuelChoiceUseCase

    @Mock
    lateinit var completeDuelSessionUseCase: CompleteDuelSessionUseCase

    @Test
    fun `Initial state is loading`() = runTest {
        // Given
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Loading)
        val viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DuelUiState.Loading)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State is InProgress when candidates loaded successfully`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Success(movies))
        val viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DuelUiState.InProgress)
            val inProgressState = state as DuelUiState.InProgress
            assertEquals(1, inProgressState.currentRound)
            assertEquals(10, inProgressState.totalRounds)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `State is Error when candidates loading fails`() = runTest {
        // Given
        val errorMessage = "Network error"
        whenever(getDuelCandidatesUseCase()).thenReturn(
            Result.Error(com.ft.architectcoders.domain.error.AppError.UnknownError(message = errorMessage))
        )
        val viewModel = createViewModel()

        // Then
        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is DuelUiState.Error)
            assertEquals(errorMessage, (state as DuelUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChooseA advances to next round`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Success(movies))
        whenever(submitDuelChoiceUseCase(any(), any())).thenAnswer { invocation ->
            val session = invocation.getArgument<com.ft.architectcoders.domain.model.DuelSession>(0)
            val choice = invocation.getArgument<com.ft.architectcoders.domain.model.DuelChoice>(1)
            session.copy(choices = session.choices + choice)
        }
        val viewModel = createViewModel()

        viewModel.state.test {
            // Initial InProgress state
            val initialState = awaitItem() as DuelUiState.InProgress
            assertEquals(1, initialState.currentRound)

            // When
            viewModel.onChooseA()

            // Then
            val nextState = awaitItem() as DuelUiState.InProgress
            assertEquals(2, nextState.currentRound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onChooseB advances to next round`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Success(movies))
        whenever(submitDuelChoiceUseCase(any(), any())).thenAnswer { invocation ->
            val session = invocation.getArgument<com.ft.architectcoders.domain.model.DuelSession>(0)
            val choice = invocation.getArgument<com.ft.architectcoders.domain.model.DuelChoice>(1)
            session.copy(choices = session.choices + choice)
        }
        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = awaitItem() as DuelUiState.InProgress
            assertEquals(1, initialState.currentRound)

            // When
            viewModel.onChooseB()

            // Then
            val nextState = awaitItem() as DuelUiState.InProgress
            assertEquals(2, nextState.currentRound)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Completing 10 rounds triggers fingerprint generation`() = runTest {
        val movies = sampleMovies(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        val fingerprint = sampleTasteFingerprint()
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Success(movies))
        whenever(submitDuelChoiceUseCase(any(), any())).thenAnswer { invocation ->
            val session = invocation.getArgument<com.ft.architectcoders.domain.model.DuelSession>(0)
            val choice = invocation.getArgument<com.ft.architectcoders.domain.model.DuelChoice>(1)
            session.copy(choices = session.choices + choice)
        }
        whenever(completeDuelSessionUseCase(any())).thenReturn(Result.Success(fingerprint))

        val viewModel = createViewModel()

        viewModel.state.test {
            // Initial state: InProgress round 1
            val initialState = awaitItem()
            assertTrue(initialState is DuelUiState.InProgress)

            // Rounds 1-9: each choice advances to next InProgress state
            repeat(9) {
                viewModel.onChooseA()
                val state = awaitItem()
                assertTrue(state is DuelUiState.InProgress)
            }

            // Round 10: triggers completion flow
            viewModel.onChooseA()

            // With UnconfinedTestDispatcher, intermediate states may be skipped
            // We need to consume all pending events and check the final state
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Completed or GeneratingFingerprint, got $finalState",
                finalState is DuelUiState.Completed || finalState is DuelUiState.GeneratingFingerprint
            )

            // If we got GeneratingFingerprint, wait for Completed
            if (finalState is DuelUiState.GeneratingFingerprint) {
                val completedState = awaitItem()
                assertTrue(completedState is DuelUiState.Completed)
                assertEquals(fingerprint, (completedState as DuelUiState.Completed).fingerprint)
            } else {
                assertEquals(fingerprint, (finalState as DuelUiState.Completed).fingerprint)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetry reloads candidates`() = runTest {
        // Given
        val movies = sampleMovies(1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20)
        whenever(getDuelCandidatesUseCase()).thenReturn(Result.Success(movies))
        whenever(submitDuelChoiceUseCase(any(), any())).thenAnswer { invocation ->
            val session = invocation.getArgument<com.ft.architectcoders.domain.model.DuelSession>(0)
            val choice = invocation.getArgument<com.ft.architectcoders.domain.model.DuelChoice>(1)
            session.copy(choices = session.choices + choice)
        }
        val viewModel = createViewModel()

        viewModel.state.test {
            // Initial InProgress state after init
            val initialState = awaitItem()
            assertTrue(initialState is DuelUiState.InProgress)
            assertEquals(1, (initialState as DuelUiState.InProgress).currentRound)

            // Make a choice to advance to round 2
            viewModel.onChooseA()
            val round2State = awaitItem()
            assertTrue(round2State is DuelUiState.InProgress)
            assertEquals(2, (round2State as DuelUiState.InProgress).currentRound)

            // When
            viewModel.onRetry()

            // With UnconfinedTestDispatcher, Loading might be skipped
            // We check the final state is InProgress at round 1
            val stateAfterRetry = expectMostRecentItem()

            // Could be Loading or InProgress depending on timing
            if (stateAfterRetry is DuelUiState.Loading) {
                val resetState = awaitItem()
                assertTrue(resetState is DuelUiState.InProgress)
                assertEquals(1, (resetState as DuelUiState.InProgress).currentRound)
            } else {
                assertTrue(stateAfterRetry is DuelUiState.InProgress)
                assertEquals(1, (stateAfterRetry as DuelUiState.InProgress).currentRound)
            }

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = DuelViewModel(
        getDuelCandidatesUseCase = getDuelCandidatesUseCase,
        submitDuelChoiceUseCase = submitDuelChoiceUseCase,
        completeDuelSessionUseCase = completeDuelSessionUseCase,
    )
}

