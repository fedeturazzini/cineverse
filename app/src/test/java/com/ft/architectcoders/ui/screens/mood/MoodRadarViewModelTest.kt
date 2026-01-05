package com.ft.architectcoders.ui.screens.mood

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.test.sampleMoodProfile
import com.ft.architectcoders.test.sampleMoodRecommendation
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.mood.BuildMoodProfileUseCase
import com.ft.architectcoders.usecases.mood.GetMoodRecommendationsUseCase
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
class MoodRadarViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var buildMoodProfileUseCase: BuildMoodProfileUseCase

    @Mock
    lateinit var getMoodRecommendationsUseCase: GetMoodRecommendationsUseCase

    @Test
    fun `Initial state is Idle with default mood vector`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            val state = awaitItem()
            assertTrue(state is MoodRadarUiState.Idle)
            assertEquals(50, state.moodVector.energy)
            assertEquals(50, state.moodVector.humor)
            assertEquals(50, state.moodVector.tension)
            assertEquals(50, state.moodVector.romance)
            assertEquals(50, state.moodVector.cerebral)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMoodChange updates energy correctly`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onMoodChange(MoodAxis.ENERGY, 80)

            val state = awaitItem()
            assertTrue(state is MoodRadarUiState.Idle)
            assertEquals(80, state.moodVector.energy)
            assertEquals(50, state.moodVector.humor)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMoodChange updates humor correctly`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onMoodChange(MoodAxis.HUMOR, 90)

            val state = awaitItem()
            assertEquals(90, state.moodVector.humor)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMoodChange clamps values to 0-100 range`() = runTest {
        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onMoodChange(MoodAxis.TENSION, 150)
            val state1 = awaitItem()
            assertEquals(100, state1.moodVector.tension)

            viewModel.onMoodChange(MoodAxis.ROMANCE, -20)
            val state2 = awaitItem()
            assertEquals(0, state2.moodVector.romance)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBuildNight transitions to Loading then Success`() = runTest {
        val profile = sampleMoodProfile()
        val recommendation = sampleMoodRecommendation(profile = profile)

        whenever(buildMoodProfileUseCase(any())).thenReturn(Result.Success(profile))
        whenever(getMoodRecommendationsUseCase(any())).thenReturn(Result.Success(recommendation))

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()
            viewModel.onBuildNight()

            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Success, got $finalState",
                finalState is MoodRadarUiState.Success
            )
            val successState = finalState as MoodRadarUiState.Success
            assertEquals(profile.microCopy, successState.profile.microCopy)
            assertEquals(recommendation.movies.size, successState.movies.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBuildNight shows Error when profile generation fails`() = runTest {
        val errorMessage = "Gemini connection failed"
        whenever(buildMoodProfileUseCase(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = errorMessage))
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onBuildNight()

            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Error, got $finalState",
                finalState is MoodRadarUiState.Error
            )
            assertEquals(errorMessage, (finalState as MoodRadarUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onBuildNight shows Error when recommendations fetch fails`() = runTest {
        val profile = sampleMoodProfile()
        val errorMessage = "TMDB connection failed"

        whenever(buildMoodProfileUseCase(any())).thenReturn(Result.Success(profile))
        whenever(getMoodRecommendationsUseCase(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = errorMessage))
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onBuildNight()

            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Error, got $finalState",
                finalState is MoodRadarUiState.Error
            )
            assertEquals(errorMessage, (finalState as MoodRadarUiState.Error).message)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRetry calls onBuildNight again`() = runTest {
        val profile = sampleMoodProfile()
        val recommendation = sampleMoodRecommendation(profile = profile)

        whenever(buildMoodProfileUseCase(any())).thenReturn(Result.Success(profile))
        whenever(getMoodRecommendationsUseCase(any())).thenReturn(Result.Success(recommendation))

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onRetry()

            val finalState = expectMostRecentItem()
            assertTrue(finalState is MoodRadarUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Changing mood after Success resets to Idle`() = runTest {
        val profile = sampleMoodProfile()
        val recommendation = sampleMoodRecommendation(profile = profile)

        whenever(buildMoodProfileUseCase(any())).thenReturn(Result.Success(profile))
        whenever(getMoodRecommendationsUseCase(any())).thenReturn(Result.Success(recommendation))

        val viewModel = createViewModel()

        viewModel.state.test {
            awaitItem()

            viewModel.onBuildNight()
            val successState = expectMostRecentItem()
            assertTrue(successState is MoodRadarUiState.Success)

            viewModel.onMoodChange(MoodAxis.ENERGY, 100)

            val idleState = awaitItem()
            assertTrue(
                "Expected Idle after mood change, got $idleState",
                idleState is MoodRadarUiState.Idle
            )
            assertEquals(100, idleState.moodVector.energy)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = MoodRadarViewModel(
        buildMoodProfileUseCase = buildMoodProfileUseCase,
        getMoodRecommendationsUseCase = getMoodRecommendationsUseCase,
    )
}

