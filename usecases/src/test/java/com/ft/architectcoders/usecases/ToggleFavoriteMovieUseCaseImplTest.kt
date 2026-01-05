package com.ft.architectcoders.usecases

import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.test.sampleMovie
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.mockito.Mockito.mock
import org.mockito.kotlin.verify

class ToggleFavoriteMovieUseCaseImplTest {
    @Test
    fun `Invoke call in repo`() =
        runTest {
            // Given
            val movie = sampleMovie(1)
            val repo = mock<MovieRepository>()
            val useCase = ToggleFavoriteMovieUseCaseImpl(repo)

            // When
            useCase(movie)

            // Then
            verify(repo).toggleFavorite(movie)
        }
}
