package com.ft.architectcoders.ui.screens.marathon

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.MarathonThemeId
import com.ft.architectcoders.test.sampleMarathonPlan
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.marathon.GenerateMarathonPlanUseCase
import com.ft.architectcoders.usecases.marathon.SaveMarathonUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
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
class MarathonTodayViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var generateMarathonPlanUseCase: GenerateMarathonPlanUseCase

    @Mock
    lateinit var saveMarathonUseCase: SaveMarathonUseCase

    @Test
    fun `Initial load shows Loading then Success`() = runTest {
        val plan = sampleMarathonPlan()
        whenever(generateMarathonPlanUseCase(any())).thenReturn(Result.Success(plan))

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Success, got $finalState",
                finalState is MarathonTodayUiState.Success,
            )
            val successState = finalState as MarathonTodayUiState.Success
            assertEquals(plan.tagline, successState.plan.tagline)
            assertEquals(3, successState.plan.picks.size)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Generate error shows Error state`() = runTest {
        val errorMessage = "TMDB connection failed"
        whenever(generateMarathonPlanUseCase(any())).thenReturn(
            Result.Error(AppError.UnknownError(message = errorMessage)),
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Error, got $finalState",
                finalState is MarathonTodayUiState.Error,
            )
            assertEquals(errorMessage, (finalState as MarathonTodayUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Empty plan shows Empty state`() = runTest {
        val emptyPlan = sampleMarathonPlan(picks = emptyList())
        whenever(generateMarathonPlanUseCase(any())).thenReturn(Result.Success(emptyPlan))

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Empty, got $finalState",
                finalState is MarathonTodayUiState.Empty,
            )
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onRegenerate fetches new plan`() = runTest {
        val plan1 = sampleMarathonPlan(tagline = "Plan 1")
        val plan2 = sampleMarathonPlan(tagline = "Plan 2")
        var callCount = 0

        whenever(generateMarathonPlanUseCase(any())).thenAnswer {
            callCount++
            if (callCount == 1) Result.Success(plan1) else Result.Success(plan2)
        }

        val viewModel = createViewModel()

        viewModel.state.test {
            val state1 = expectMostRecentItem()
            assertTrue(state1 is MarathonTodayUiState.Success)
            assertEquals("Plan 1", (state1 as MarathonTodayUiState.Success).plan.tagline)

            viewModel.onRegenerate()

            val state2 = expectMostRecentItem()
            assertTrue(state2 is MarathonTodayUiState.Success)
            assertEquals("Plan 2", (state2 as MarathonTodayUiState.Success).plan.tagline)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onSave marks plan as saved`() = runTest {
        val plan = sampleMarathonPlan()
        whenever(generateMarathonPlanUseCase(any())).thenReturn(Result.Success(plan))
        whenever(saveMarathonUseCase(any())).thenReturn(1L)

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()
            assertTrue(initialState is MarathonTodayUiState.Success)
            assertFalse((initialState as MarathonTodayUiState.Success).isSaved)

            viewModel.onSave()

            val savedState = expectMostRecentItem()
            assertTrue(savedState is MarathonTodayUiState.Success)
            assertTrue((savedState as MarathonTodayUiState.Success).isSaved)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = MarathonTodayViewModel(
        themeId = MarathonThemeId.LAUGH,
        generateMarathonPlanUseCase = generateMarathonPlanUseCase,
        saveMarathonUseCase = saveMarathonUseCase,
    )
}

