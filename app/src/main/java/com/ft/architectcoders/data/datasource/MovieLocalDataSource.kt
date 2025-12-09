package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.data.datasource.database.MoviesDao
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Movie

class MovieLocalDataSource(private val moviesDao: MoviesDao) {
    val movies = moviesDao.fetchMovies()

    fun findMovieById(id: Int) = moviesDao.findMovieById(id)

    suspend fun countMovies() = moviesDao.countMovies()

    suspend fun saveMovies(movies: List<Movie>) = moviesDao.saveMovies(movies)

    suspend fun isEmpty() = countMovies() == 0

    suspend fun clearMovies() = moviesDao.clearMovies()

    suspend fun updateAiReview(
        movieId: Int,
        review: AiReview,
    ) = moviesDao.updateAiReview(movieId, review.rating, review.quote)
}
