package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import com.ft.architectcoders.domain.Result

interface MovieRemoteDataSource {
    suspend fun fetchPopularMovies(): Result<List<Movie>>

    suspend fun findMovieById(id: Int): Result<Movie>

    suspend fun fetchMovieCredits(movieId: Int): Result<List<Cast>>

    suspend fun fetchMovieVideos(movieId: Int): Result<List<MovieVideo>>

    suspend fun searchMovies(query: String): Result<List<Movie>>
}
