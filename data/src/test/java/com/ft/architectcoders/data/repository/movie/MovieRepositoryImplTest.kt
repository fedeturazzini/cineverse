package com.ft.architectcoders.data.repository.movie

import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.test.errorResult
import com.ft.architectcoders.test.loadingResult
import com.ft.architectcoders.test.sampleCast
import com.ft.architectcoders.test.sampleHttpError
import com.ft.architectcoders.test.sampleMovie
import com.ft.architectcoders.test.sampleMovieVideo
import com.ft.architectcoders.test.sampleMovies
import com.ft.architectcoders.test.successResult
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.argThat
import org.mockito.kotlin.never
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

@RunWith(MockitoJUnitRunner::class)
class MovieRepositoryImplTest {
    @Mock
    lateinit var remoteDataSource: MovieRemoteDataSource

    @Mock
    lateinit var localDataSource: MovieLocalDataSource
    private lateinit var repository: MovieRepository

    @Before
    fun setUp() {
        repository =
            MovieRepositoryImpl(
                remoteDataSource = remoteDataSource,
                localDataSource = localDataSource,
            )
    }

    @Test
    fun `Popular movies are taken from local data source`() =
        runTest {
            // Given
            val localMovies = sampleMovies(1, 2, 3)
            whenever(localDataSource.movies).thenReturn(flowOf(localMovies))

            // When
            val result = repository.movies

            // Then
            assertEquals(localMovies, result.first())
        }

    @Test
    fun `Popular movies are saved to local data source when it is empty`() =
        runTest {
            // Given
            val localMovies = emptyList<Movie>()
            val remoteMovies = sampleMovies(1, 2)
            whenever(localDataSource.movies).thenReturn(flowOf(localMovies))
            whenever(remoteDataSource.fetchPopularMovies()).thenReturn(successResult(remoteMovies))

            // When
            repository.movies.first()

            // Then
            verify(localDataSource).saveMovies(remoteMovies)
        }

    @Test
    fun `Toggling favorite update local data source`() =
        runTest {
            // Given
            val movie = sampleMovie(2)

            // When
            repository.toggleFavorite(movie)

            // Then
            verify(localDataSource).saveMovies(
                argThat { get(0).id == 2 },
            )
        }

    @Test
    fun `Switching favorite marks as favorite an unfavorite movie`() =
        runTest {
            // Given
            val movie = sampleMovie(1).copy(favorite = false)

            // When
            repository.toggleFavorite(movie)

            // Then
            verify(localDataSource).saveMovies(
                argThat { get(0).favorite },
            )
        }

    @Test
    fun `Switching favorite marks as unfavorite a favorite movie`() =
        runTest {
            // Given
            val movie = sampleMovie(1).copy(favorite = true)

            // When
            repository.toggleFavorite(movie)

            // Then
            verify(localDataSource).saveMovies(
                argThat { !get(0).favorite },
            )
        }

    @Test
    fun `findMovieById returns local movie when available`() =
        runTest {
            // Given
            val movieId = 1
            val localMovie = sampleMovie(movieId)
            whenever(localDataSource.findMovieById(movieId)).thenReturn(flowOf(localMovie))

            // When
            val result = repository.findMovieById(movieId).first()

            // Then
            assertTrue(result is Result.Success)
            assertEquals(localMovie, (result as Result.Success).data)
        }

    @Test
    fun `findMovieById fetches from remote and saves when local is empty`() =
        runTest {
            // Given
            val movieId = 1
            val remoteMovie = sampleMovie(movieId)
            whenever(localDataSource.findMovieById(movieId))
                .thenReturn(flowOf())
                .thenReturn(flowOf(remoteMovie))
            whenever(remoteDataSource.findMovieById(movieId)).thenReturn(successResult(remoteMovie))

            // When
            val result = repository.findMovieById(movieId).first()

            // Then
            verify(localDataSource).saveMovies(listOf(remoteMovie))
            assertTrue(result is Result.Success)
            assertEquals(remoteMovie, (result as Result.Success).data)
        }

    @Test
    fun `findMovieById returns remote error when local is empty`() =
        runTest {
            // Given
            val movieId = 1
            val error = sampleHttpError()
            whenever(localDataSource.findMovieById(movieId)).thenReturn(flowOf())
            whenever(remoteDataSource.findMovieById(movieId)).thenReturn(errorResult(error))

            // When
            val result = repository.findMovieById(movieId).first()

            // Then
            assertTrue(result is Result.Error)
            assertEquals(error, (result as Result.Error).error)
        }

    @Test
    fun `findMovieById returns remote loading when local is empty`() =
        runTest {
            // Given
            val movieId = 1
            whenever(localDataSource.findMovieById(movieId)).thenReturn(flowOf())
            whenever(remoteDataSource.findMovieById(movieId)).thenReturn(loadingResult())

            // When
            val result = repository.findMovieById(movieId).first()

            // Then
            assertTrue(result is Result.Loading)
        }

    @Test
    fun `getMovieCredits returns credits from remote data source`() =
        runTest {
            // Given
            val movieId = 1
            val credits = listOf(sampleCast(1), sampleCast(2))
            whenever(remoteDataSource.fetchMovieCredits(movieId)).thenReturn(successResult(credits))

            // When
            val result = repository.getMovieCredits(movieId).first()

            // Then
            assertTrue(result is Result.Success)
            assertEquals(credits, (result as Result.Success).data)
        }

    @Test
    fun `getMovieCredits returns error from remote data source`() =
        runTest {
            // Given
            val movieId = 1
            val error = sampleHttpError()
            whenever(remoteDataSource.fetchMovieCredits(movieId)).thenReturn(errorResult(error))

            // When
            val result = repository.getMovieCredits(movieId).first()

            // Then
            assertTrue(result is Result.Error)
            assertEquals(error, (result as Result.Error).error)
        }

    @Test
    fun `getMovieVideos returns videos from remote data source`() =
        runTest {
            // Given
            val movieId = 1
            val videos = listOf(sampleMovieVideo("1"), sampleMovieVideo("2"))
            whenever(remoteDataSource.fetchMovieVideos(movieId)).thenReturn(successResult(videos))

            // When
            val result = repository.getMovieVideos(movieId).first()

            // Then
            assertTrue(result is Result.Success)
            assertEquals(videos, (result as Result.Success).data)
        }

    @Test
    fun `getMovieVideos returns error from remote data source`() =
        runTest {
            // Given
            val movieId = 1
            val error = sampleHttpError()
            whenever(remoteDataSource.fetchMovieVideos(movieId)).thenReturn(errorResult(error))

            // When
            val result = repository.getMovieVideos(movieId).first()

            // Then
            assertTrue(result is Result.Error)
            assertEquals(error, (result as Result.Error).error)
        }

    // Tests para searchMovies
    @Test
    fun `searchMovies returns movies list when remote succeeds`() =
        runTest {
            // Given
            val query = "test"
            val movies = sampleMovies(1, 2, 3)
            whenever(remoteDataSource.searchMovies(query)).thenReturn(successResult(movies))

            // When
            val result = repository.searchMovies(query)

            // Then
            assertEquals(movies, result)
        }

    @Test
    fun `searchMovies returns empty list when remote fails`() =
        runTest {
            // Given
            val query = "test"
            val error = sampleHttpError()
            whenever(remoteDataSource.searchMovies(query)).thenReturn(errorResult(error))

            // When
            val result = repository.searchMovies(query)

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `searchMovies returns empty list when remote is loading`() =
        runTest {
            // Given
            val query = "test"
            whenever(remoteDataSource.searchMovies(query)).thenReturn(loadingResult())

            // When
            val result = repository.searchMovies(query)

            // Then
            assertTrue(result.isEmpty())
        }

    @Test
    fun `Popular movies does not fetch from remote when local has data`() =
        runTest {
            // Given
            val localMovies = sampleMovies(1, 2, 3)
            whenever(localDataSource.movies).thenReturn(flowOf(localMovies))

            // When
            repository.movies.first()

            // Then
            verify(remoteDataSource, never()).fetchPopularMovies()
        }

    @Test
    fun `Popular movies does not save when remote returns error`() =
        runTest {
            // Given
            val localMovies = emptyList<Movie>()
            val error = sampleHttpError()
            whenever(localDataSource.movies).thenReturn(flowOf(localMovies))
            whenever(remoteDataSource.fetchPopularMovies()).thenReturn(errorResult(error))

            // When
            repository.movies.first()

            // Then
            verify(localDataSource, never()).saveMovies(argThat { true })
        }

    @Test
    fun `Popular movies does not save when remote returns loading`() =
        runTest {
            // Given
            val localMovies = emptyList<Movie>()
            whenever(localDataSource.movies).thenReturn(flowOf(localMovies))
            whenever(remoteDataSource.fetchPopularMovies()).thenReturn(loadingResult())

            // When
            repository.movies.first()

            // Then
            verify(localDataSource, never()).saveMovies(argThat { true })
        }
}
