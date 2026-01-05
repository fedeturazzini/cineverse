package com.ft.architectcoders.ui.screens.detail

import app.cash.turbine.test
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.test.errorResult
import com.ft.architectcoders.test.sampleAiReview
import com.ft.architectcoders.test.sampleCast
import com.ft.architectcoders.test.sampleMovie
import com.ft.architectcoders.test.sampleMovieVideo
import com.ft.architectcoders.test.sampleUnknownError
import com.ft.architectcoders.test.successResult
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.FindMovieByIdUseCase
import com.ft.architectcoders.usecases.GetMovieCreditsUseCase
import com.ft.architectcoders.usecases.GetMovieVideosUseCase
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCase
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(MockitoJUnitRunner::class)
class MovieDetailViewModelTest {
    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Mock
    lateinit var findMovieByIdUseCase: FindMovieByIdUseCase

    @Mock
    lateinit var getMovieCreditsUseCase: GetMovieCreditsUseCase

    @Mock
    lateinit var getMovieVideosUseCase: GetMovieVideosUseCase

    @Mock
    lateinit var toggleFavoriteMovieUseCase: ToggleFavoriteMovieUseCase

    @Mock
    lateinit var geminiRepository: GeminiRepository

    private val movieId = 1

    private fun buildViewModel() =
        MovieDetailViewModel(
            movieId = movieId,
            findMovieByIdUseCase = findMovieByIdUseCase,
            getMovieCreditsUseCase = getMovieCreditsUseCase,
            getMovieVideosUseCase = getMovieVideosUseCase,
            toggleFavoriteMovieUseCase = toggleFavoriteMovieUseCase,
            geminiRepository = geminiRepository,
        )

    private fun setupSuccessMocks() {
        val movie = sampleMovie(movieId)
        whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
        whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(listOf(sampleCast(1)))))
        whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(listOf(sampleMovieVideo("1")))))
        whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(sampleAiReview())))
    }

    @Test
    fun `Initial state is loading`() =
        runTest {
            // Given
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(Result.Loading))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(true, state.isLoadingMovie)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Movie is loaded successfully`() =
        runTest {
            // Given
            val movie = sampleMovie(movieId)
            setupSuccessMocks()

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(movie, state.movie)
                assertEquals(false, state.isLoadingMovie)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Cast is loaded successfully`() =
        runTest {
            // Given
            val cast = listOf(sampleCast(1), sampleCast(2))
            val movie = sampleMovie(movieId)
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(cast)))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(sampleAiReview())))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(cast, state.cast)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Videos are loaded successfully`() =
        runTest {
            // Given
            val videos = listOf(sampleMovieVideo("1"), sampleMovieVideo("2"))
            val movie = sampleMovie(movieId)
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(videos)))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(sampleAiReview())))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(videos, state.videos)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `AI Review is loaded successfully`() =
        runTest {
            // Given
            val aiReview = sampleAiReview(rating = 4.8f, quote = "Masterpiece")
            val movie = sampleMovie(movieId)
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(aiReview)))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                val aiReviewState = state.aiReviewState
                assert(aiReviewState is AiReviewUiState.Success)
                assertEquals(aiReview, (aiReviewState as AiReviewUiState.Success).aiReview)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Error loading movie shows generic error`() =
        runTest {
            // Given
            val error = sampleUnknownError(message = "Movie not found")
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(errorResult(error)))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertEquals(false, state.isLoadingMovie)
                assertNotNull(state.error)
                assertEquals("Movie not found", state.error?.genericError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Error loading cast shows cast error`() =
        runTest {
            // Given
            val movie = sampleMovie(movieId)
            val castError = sampleUnknownError(message = "Cast not found")
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(errorResult(castError)))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(sampleAiReview())))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertNotNull(state.error)
                assertEquals("Cast not found", state.error?.castError)
                assertNull(state.error?.genericError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Error loading videos shows videos error`() =
        runTest {
            // Given
            val movie = sampleMovie(movieId)
            val videosError = sampleUnknownError(message = "Videos not found")
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(errorResult(videosError)))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(successResult(sampleAiReview())))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                assertNotNull(state.error)
                assertEquals("Videos not found", state.error?.videosError)
                assertNull(state.error?.genericError)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Error in AI Review shows error state`() =
        runTest {
            // Given
            val movie = sampleMovie(movieId)
            val aiError = sampleUnknownError(message = "AI service unavailable")
            whenever(findMovieByIdUseCase(movieId)).thenReturn(flowOf(successResult(movie)))
            whenever(getMovieCreditsUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(getMovieVideosUseCase(movieId)).thenReturn(flowOf(successResult(emptyList())))
            whenever(geminiRepository.getMovieReview(any(), any(), any())).thenReturn(flowOf(errorResult(aiError)))

            // When
            val viewModel = buildViewModel()

            // Then
            viewModel.state.test {
                val state = awaitItem()
                val aiReviewState = state.aiReviewState
                assert(aiReviewState is AiReviewUiState.Error)
                assertEquals("AI service unavailable", (aiReviewState as AiReviewUiState.Error).message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `Toggle favorite invokes use case`() =
        runTest {
            // Given
            val movie = sampleMovie(movieId)
            setupSuccessMocks()
            val viewModel = buildViewModel()

            viewModel.state.test {
                awaitItem()

                // When
                viewModel.onFavoriteClick()

                // Then
                verify(toggleFavoriteMovieUseCase).invoke(movie)

                cancelAndIgnoreRemainingEvents()
            }
        }
}
