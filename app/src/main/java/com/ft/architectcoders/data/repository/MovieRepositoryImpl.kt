package com.ft.architectcoders.data.repository

import com.ft.architectcoders.data.remote.tmdb.TmdbService
import com.ft.architectcoders.data.remote.tmdb.mapper.toDomain
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.repository.MovieRepository

class MovieRepositoryImpl(
    private val tmdbService: TmdbService,
) : MovieRepository {
    override suspend fun fetchPopularMovies(region: String): List<Movie> {
        return try {
            tmdbService
                .getPopularMovies(region)
                .results
                .map { it.toDomain() }
        } catch (e: Exception) {
            // Manejar errores mas adelante
            throw e
        }
    }

    override suspend fun findMovieById(id: Int): Movie {
        return try {
            tmdbService
                .getMovieById(id)
                .toDomain()
        } catch (e: Exception) {
            // Manejar errores mas adelante
            throw e
        }
    }
}
