package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.Flow

interface MovieLocalDataSource {
    val movies: Flow<List<Movie>>

    fun findMovieById(id: Int): Flow<Movie>

    suspend fun countMovies(): Int
    suspend fun saveMovies(movies: List<Movie>)
    suspend fun isEmpty(): Boolean
    suspend fun clearMovies()

    suspend fun updateAiReview(
        movieId: Int,
        review: AiReview,
    )
}

