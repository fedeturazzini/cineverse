package com.ft.architectcoders.ui.screens.wrap

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.CineverseWrap
import com.ft.architectcoders.domain.model.WrapArchetype
import com.ft.architectcoders.domain.model.WrapPeriod
import com.ft.architectcoders.domain.model.WrapSectionCopy
import com.ft.architectcoders.domain.model.WrapStats
import com.ft.architectcoders.domain.model.WrapTotals
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.wrap.CineverseWrapResult
import com.ft.architectcoders.usecases.wrap.GetCineverseWrapUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class CineverseWrapViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var getCineverseWrapUseCase: GetCineverseWrapUseCase

    @Test
    fun `Initial load shows Loading then Success with wrap data`() = runTest {
        val wrap = sampleWrap()
        whenever(getCineverseWrapUseCase()).thenReturn(
            Result.Success(CineverseWrapResult.Success(wrap)),
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Success, got $finalState",
                finalState is CineverseWrapUiState.Success,
            )
            val successState = finalState as CineverseWrapUiState.Success
            assertEquals(wrap.funnyProfileSummary, successState.wrap.funnyProfileSummary)
            assertEquals(wrap.archetype.name, successState.wrap.archetype.name)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Empty state when not enough interactions`() = runTest {
        whenever(getCineverseWrapUseCase()).thenReturn(
            Result.Success(CineverseWrapResult.Empty(interactionsNeeded = 3)),
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Empty, got $finalState",
                finalState is CineverseWrapUiState.Empty,
            )
            val emptyState = finalState as CineverseWrapUiState.Empty
            assertEquals(3, emptyState.interactionsNeeded)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error state on use case failure`() = runTest {
        val errorMessage = "Error generating wrap"
        whenever(getCineverseWrapUseCase()).thenReturn(
            Result.Error(AppError.UnknownError(message = errorMessage)),
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            val finalState = expectMostRecentItem()
            assertTrue(
                "Expected Error, got $finalState",
                finalState is CineverseWrapUiState.Error,
            )
            assertEquals(errorMessage, (finalState as CineverseWrapUiState.Error).message)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Share click emits ShareText event with correct content`() = runTest {
        val wrap = sampleWrap(
            funnyProfileSummary = "Test summary",
            shareText = "Share this #Cineverso60s",
        )
        whenever(getCineverseWrapUseCase()).thenReturn(
            Result.Success(CineverseWrapResult.Success(wrap)),
        )

        val viewModel = createViewModel()

        // Wait for initial load
        viewModel.state.test {
            expectMostRecentItem()
            cancelAndIgnoreRemainingEvents()
        }

        viewModel.events.test {
            viewModel.onShareClick()

            val event = awaitItem()
            assertTrue(event is CineverseWrapEvent.ShareText)
            val shareEvent = event as CineverseWrapEvent.ShareText
            assertTrue(shareEvent.text.contains("Test summary"))
            assertTrue(shareEvent.text.contains("Share this #Cineverso60s"))

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Retry reloads wrap`() = runTest {
        var callCount = 0
        whenever(getCineverseWrapUseCase()).thenAnswer {
            callCount++
            if (callCount == 1) {
                Result.Error(AppError.UnknownError(message = "Error"))
            } else {
                Result.Success(CineverseWrapResult.Success(sampleWrap()))
            }
        }

        val viewModel = createViewModel()

        viewModel.state.test {
            // Initial error
            var state = expectMostRecentItem()
            assertTrue(state is CineverseWrapUiState.Error)

            // Retry
            viewModel.onRetry()

            state = expectMostRecentItem()
            assertTrue(state is CineverseWrapUiState.Success)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = CineverseWrapViewModel(
        getCineverseWrapUseCase = getCineverseWrapUseCase,
    )

    private fun sampleWrap(
        funnyProfileSummary: String = "Tu Cineverso es Drama puro.",
        shareText: String = "Mi arquetipo: Explorador. #Cineverso60s",
    ) = CineverseWrap(
        period = WrapPeriod(
            fromDate = "2025-01-01",
            toDate = "2025-01-31",
        ),
        funnyProfileSummary = funnyProfileSummary,
        archetype = WrapArchetype(
            name = "Explorador Nostálgico",
            tagline = "Siempre en busca de algo nuevo",
            bullets = listOf("Bullet 1", "Bullet 2", "Bullet 3"),
        ),
        sectionCopy = WrapSectionCopy(
            genresLine = "Tus géneros top: Drama, Thriller",
            moviesLine = "Tus infaltables:",
            aiLine = null,
            actorLine = null,
            directorLine = null,
        ),
        shareText = shareText,
        stats = WrapStats(
            period = WrapPeriod("2025-01-01", "2025-01-31"),
            totals = WrapTotals(moviesViewed = 10, favorites = 5, aiSearchSessions = 2),
            topGenres = emptyList(),
            topMovies = emptyList(),
            favoritesHighlights = emptyList(),
            topActors = emptyList(),
            topDirectors = emptyList(),
            aiSearchHighlights = null,
        ),
        generatedByAi = true,
    )
}
