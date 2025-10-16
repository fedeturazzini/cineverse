package com.ft.architectcoders.domain.repository

import com.ft.architectcoders.domain.model.Movie

interface MovieRepository {
    suspend fun fetchPopularMovies(region: String): List<Movie>

    suspend fun findMovieById(id: Int): Movie
}
