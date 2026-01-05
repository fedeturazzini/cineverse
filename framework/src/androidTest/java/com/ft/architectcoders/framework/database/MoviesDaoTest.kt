package com.ft.architectcoders.framework.database

import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MoviesDaoTest {

    private lateinit var database: CineVerseDatabase
    private lateinit var moviesDao: MoviesDao

    @Before
    fun setUp() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            CineVerseDatabase::class.java
        ).allowMainThreadQueries().build()
        moviesDao = database.moviesDao
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun saveAndFetchMovies_returnsCorrectData() = runTest {
        // Given
        val movies = listOf(sampleDbMovie(1), sampleDbMovie(2))

        // When
        moviesDao.saveMovies(movies)

        // Then
        val result = moviesDao.fetchMovies().first()
        assertEquals(2, result.size)
    }

    @Test
    fun countMovies_returnsCorrectCount() = runTest {
        // Given
        val movies = listOf(sampleDbMovie(1), sampleDbMovie(2), sampleDbMovie(3))
        moviesDao.saveMovies(movies)

        // When
        val count = moviesDao.countMovies()

        // Then
        assertEquals(3, count)
    }

    @Test
    fun findMovieById_returnsCorrectMovie() = runTest {
        // Given
        val movies = listOf(sampleDbMovie(1), sampleDbMovie(2))
        moviesDao.saveMovies(movies)

        // When
        val movie = moviesDao.findMovieById(2).first()

        // Then
        assertEquals(2, movie?.id)
        assertEquals("Movie 2", movie?.title)
    }

    @Test
    fun clearMovies_removesAllMovies() = runTest {
        // Given
        val movies = listOf(sampleDbMovie(1), sampleDbMovie(2))
        moviesDao.saveMovies(movies)

        // When
        moviesDao.clearMovies()

        // Then
        val count = moviesDao.countMovies()
        assertEquals(0, count)
    }

    private fun sampleDbMovie(id: Int) = DbMovie(
        id = id,
        title = "Movie $id",
        originalTitle = "Original Movie $id",
        poster = "/poster$id.jpg",
        backdrop = "/backdrop$id.jpg",
        releaseDate = "2024-01-0$id",
        overview = "Overview for movie $id",
        favorite = false,
        aiRating = null,
        aiQuote = null
    )
}

