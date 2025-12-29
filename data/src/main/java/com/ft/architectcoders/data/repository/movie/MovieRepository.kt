package com.ft.architectcoders.data.repository.movie

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import kotlinx.coroutines.flow.Flow

interface MovieRepository {
    val movies: Flow<List<Movie>>

    fun findMovieById(id: Int): Flow<Result<Movie>>

    fun getMovieCredits(movieId: Int): Flow<Result<List<Cast>>>

    suspend fun toggleFavorite(movie: Movie)

    fun getMovieVideos(movieId: Int): Flow<Result<List<MovieVideo>>>

    suspend fun searchMovies(query: String): List<Movie>
}
