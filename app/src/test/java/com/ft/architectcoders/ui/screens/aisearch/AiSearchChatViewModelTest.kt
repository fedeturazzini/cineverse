package com.ft.architectcoders.ui.screens.aisearch

import app.cash.turbine.test
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.error.AppError
import com.ft.architectcoders.domain.model.AiSearchSession
import com.ft.architectcoders.domain.model.AiSearchTurnResult
import com.ft.architectcoders.domain.model.AiSearchUserSignals
import com.ft.architectcoders.domain.model.ChatRole
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.test.sampleAiSearchTurnResult
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.aisearch.SaveAiSearchSessionUseCase
import com.ft.architectcoders.usecases.aisearch.SendAiSearchMessageUseCase
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
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner.Silent::class)
class AiSearchChatViewModelTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var sendAiSearchMessageUseCase: SendAiSearchMessageUseCase

    @Mock
    lateinit var saveAiSearchSessionUseCase: SaveAiSearchSessionUseCase

    @Mock
    lateinit var fetchMoviesUseCase: FetchMoviesUseCase

    @Test
    fun `Initial state shows welcome message`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        viewModel.state.test {
            val state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            val chatState = state as AiSearchChatUiState.Chatting
            assertEquals(1, chatState.messages.size)
            assertEquals(ChatRole.ASSISTANT, chatState.messages.first().role)
            assertEquals(0, chatState.questionsUsed)
            assertFalse(chatState.isProcessing)
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `After 3 messages state changes to Completed`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult())
        )
        whenever(saveAiSearchSessionUseCase(any())).thenReturn(1L)

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("First question")
            advanceUntilIdle()
            var state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            assertEquals(1, (state as AiSearchChatUiState.Chatting).questionsUsed)

            viewModel.onUserSend("Second question")
            advanceUntilIdle()
            state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            assertEquals(2, (state as AiSearchChatUiState.Chatting).questionsUsed)

            whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
                Result.Success(sampleAiSearchTurnResult(finalBullets = listOf("Bullet 1", "Bullet 2")))
            )
            viewModel.onUserSend("Third question")
            advanceUntilIdle()
            state = expectMostRecentItem()
            assertTrue("Expected Completed state, got $state", state is AiSearchChatUiState.Completed)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Cannot send more than 3 messages`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult())
        )
        whenever(saveAiSearchSessionUseCase(any())).thenReturn(1L)

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("First")
            advanceUntilIdle()
            expectMostRecentItem()

            viewModel.onUserSend("Second")
            advanceUntilIdle()
            expectMostRecentItem()

            whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
                Result.Success(sampleAiSearchTurnResult(finalBullets = listOf("Done")))
            )
            viewModel.onUserSend("Third")
            advanceUntilIdle()
            val completedState = expectMostRecentItem()
            assertTrue(completedState is AiSearchChatUiState.Completed)

            viewModel.onUserSend("Fourth - should be ignored")
            advanceUntilIdle()

            val finalState = viewModel.state.value
            assertTrue(finalState is AiSearchChatUiState.Completed)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Out of scope message does not count as question`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult(isOutOfScope = true))
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("What is the weather?")
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            val chatState = state as AiSearchChatUiState.Chatting
            assertEquals(0, chatState.questionsUsed)
            assertTrue(chatState.messages.any { 
                it.content.contains("Solo puedo ayudarte a elegir películas") 
            })

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Valid message shows results`() = runTest {
        val movies = sampleMovies(1, 2, 3, 4, 5)
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult(movies = movies))
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("I want action movies")
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            val chatState = state as AiSearchChatUiState.Chatting
            assertEquals(5, chatState.currentResults.size)
            assertEquals(1, chatState.questionsUsed)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Error from use case shows error message in chat`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Error(AppError.UnknownError(message = "API Error"))
        )

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("Test message")
            advanceUntilIdle()

            val state = expectMostRecentItem()
            assertTrue(state is AiSearchChatUiState.Chatting)
            val chatState = state as AiSearchChatUiState.Chatting
            assertTrue(chatState.messages.any { it.content == "API Error" })
            assertFalse(chatState.isProcessing)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Session is saved on completion`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult())
        )
        whenever(saveAiSearchSessionUseCase(any())).thenReturn(1L)

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("First")
            advanceUntilIdle()
            expectMostRecentItem()

            viewModel.onUserSend("Second")
            advanceUntilIdle()
            expectMostRecentItem()

            whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
                Result.Success(sampleAiSearchTurnResult(finalBullets = listOf("Done")))
            )
            viewModel.onUserSend("Third")
            advanceUntilIdle()

            verify(saveAiSearchSessionUseCase).invoke(any())

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Restart creates new session`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))
        whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
            Result.Success(sampleAiSearchTurnResult())
        )
        whenever(saveAiSearchSessionUseCase(any())).thenReturn(1L)

        val viewModel = createViewModel()

        viewModel.state.test {
            expectMostRecentItem()

            viewModel.onUserSend("First")
            advanceUntilIdle()
            expectMostRecentItem()

            viewModel.onUserSend("Second")
            advanceUntilIdle()
            expectMostRecentItem()

            whenever(sendAiSearchMessageUseCase(any(), any(), any())).thenReturn(
                Result.Success(sampleAiSearchTurnResult(finalBullets = listOf("Done")))
            )
            viewModel.onUserSend("Third")
            advanceUntilIdle()
            val completedState = expectMostRecentItem()
            assertTrue(completedState is AiSearchChatUiState.Completed)

            viewModel.onRestart()

            val newState = expectMostRecentItem()
            assertTrue(newState is AiSearchChatUiState.Chatting)
            val chatState = newState as AiSearchChatUiState.Chatting
            assertEquals(0, chatState.questionsUsed)
            assertEquals(1, chatState.messages.size)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `onMovieClick emits navigation event`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        viewModel.events.test {
            viewModel.onMovieClick(123)

            val event = awaitItem()
            assertTrue(event is AiSearchChatEvent.NavigateToMovieDetail)
            assertEquals(123, (event as AiSearchChatEvent.NavigateToMovieDetail).movieId)

            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `Empty message is ignored`() = runTest {
        whenever(fetchMoviesUseCase()).thenReturn(flowOf(emptyList()))

        val viewModel = createViewModel()

        viewModel.state.test {
            val initialState = expectMostRecentItem()

            viewModel.onUserSend("")
            advanceUntilIdle()

            viewModel.onUserSend("   ")
            advanceUntilIdle()

            verify(sendAiSearchMessageUseCase, never()).invoke(any(), any(), any())

            val finalState = viewModel.state.value
            assertEquals(initialState, finalState)

            cancelAndIgnoreRemainingEvents()
        }
    }

    private fun createViewModel() = AiSearchChatViewModel(
        sendAiSearchMessageUseCase = sendAiSearchMessageUseCase,
        saveAiSearchSessionUseCase = saveAiSearchSessionUseCase,
        fetchMoviesUseCase = fetchMoviesUseCase,
    )
}

