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

    suspend fun discoverMovies(
        genres: List<Int>? = null,
        excludeGenres: List<Int>? = null,
        sortBy: String = "popularity.desc",
        minVoteAverage: Float? = null,
        yearFrom: Int? = null,
        yearTo: Int? = null,
    ): Result<List<Movie>>
}
