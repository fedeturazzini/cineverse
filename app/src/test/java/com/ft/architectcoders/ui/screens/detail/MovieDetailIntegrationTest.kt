package com.ft.architectcoders.ui.screens.detail

import app.cash.turbine.test
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.test.data.FakeGeminiRepository
import com.ft.architectcoders.test.data.FakeLocalDataSource
import com.ft.architectcoders.test.data.FakeRemoteDataSource
import com.ft.architectcoders.test.data.buildMoviesRepositoryWith
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.testrules.CoroutinesTestRule
import com.ft.architectcoders.usecases.FindMovieByIdUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieCreditsUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieVideosUseCaseImpl
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCaseImpl
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test

class MovieDetailIntegrationTest {

    @get:Rule
    val coroutinesTestRule = CoroutinesTestRule()

    @Test
    fun `UI is updated with the movie on start`() = runTest {
        val moviesRepository = buildMoviesRepositoryWith(localData = sampleMovies(1, 2, 3, 4))
        val viewModel = buildDetailViewModel(2, moviesRepository)

        viewModel.state.test {
            val state = awaitItem()
            assertEquals(sampleMovies(2).first(), state.movie)
            assertEquals(false, state.isLoadingMovie)
            assertTrue(state.aiReviewState is AiReviewUiState.Success)
        }
    }

    @Test
    fun `Favorite updated in local data source`() = runTest {
        val local = FakeLocalDataSource().apply {
            inMemoryMovies.value = sampleMovies(1, 2, 3, 4)
        }
        val moviesRepository = MovieRepositoryImpl(FakeRemoteDataSource(), local)
        val viewModel = buildDetailViewModel(2, moviesRepository)

        viewModel.state.test {
            val beforeToggle = awaitItem()
            assertEquals(false, beforeToggle.movie?.favorite)

            viewModel.onFavoriteClick()

            val afterToggle = awaitItem()
            assertEquals(true, afterToggle.movie?.favorite)
        }

        assertEquals(true, local.inMemoryMovies.value.first { it.id == 2 }.favorite)
    }
}

private fun buildDetailViewModel(
    movieId: Int,
    moviesRepository: MovieRepository
) = MovieDetailViewModel(
    movieId,
    findMovieByIdUseCase = FindMovieByIdUseCaseImpl(moviesRepository),
    getMovieCreditsUseCase = GetMovieCreditsUseCaseImpl(moviesRepository),
    getMovieVideosUseCase = GetMovieVideosUseCaseImpl(moviesRepository),
    toggleFavoriteMovieUseCase = ToggleFavoriteMovieUseCaseImpl(moviesRepository),
    geminiRepository = FakeGeminiRepository()
)
