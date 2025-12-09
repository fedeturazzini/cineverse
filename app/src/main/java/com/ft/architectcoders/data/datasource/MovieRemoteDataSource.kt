package com.ft.architectcoders.data.datasource

import com.ft.architectcoders.Result
import com.ft.architectcoders.data.error.toTmdbResult
import com.ft.architectcoders.data.datasource.remote.tmdb.TmdbService
import com.ft.architectcoders.data.datasource.remote.tmdb.mapper.toDomain
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo

interface MovieRemoteDataSource {
    suspend fun fetchPopularMovies(): Result<List<Movie>>
    suspend fun findMovieById(id: Int): Result<Movie>
    suspend fun fetchMovieCredits(movieId: Int): Result<List<Cast>>
    suspend fun fetchMovieVideos(movieId: Int): Result<List<MovieVideo>>
    suspend fun searchMovies(query: String): Result<List<Movie>>
}

class MovieRemoteDataSourceImpl(
    private val tmdbService: TmdbService,
    private val regionRepository: RegionRepository,
) : MovieRemoteDataSource {
    override suspend fun fetchPopularMovies(): Result<List<Movie>> {
        return try {
            val response = tmdbService.getPopularMovies(regionRepository.findLastRegion())
            Result.Success(response.results.map { it.toDomain() })
        } catch (e: Exception) {
            e.toTmdbResult()
        }
    }

    override suspend fun findMovieById(id: Int): Result<Movie> {
        return try {
            val response = tmdbService.getMovieById(id)
            Result.Success(response.toDomain())
        } catch (e: Exception) {
            e.toTmdbResult()
        }
    }

    override suspend fun fetchMovieCredits(movieId: Int): Result<List<Cast>> {
        return try {
            val response = tmdbService.getMovieCredits(movieId)
            Result.Success(response.cast.map { it.toDomain() })
        } catch (e: Exception) {
            e.toTmdbResult()
        }
    }

    override suspend fun fetchMovieVideos(movieId: Int): Result<List<MovieVideo>> {
        return try {
            val response = tmdbService.getMovieVideos(movieId)
            Result.Success(response.results.map { it.toDomain() }.filter { it.isYouTube })
        } catch (e: Exception) {
            e.toTmdbResult()
        }
    }

    override suspend fun searchMovies(query: String): Result<List<Movie>> {
        return try {
            if (query.isBlank()) {
                Result.Success(emptyList())
            } else {
                val response = tmdbService.searchMovies(
                    query = query,
                    region = regionRepository.findLastRegion()
                )
                Result.Success(response.results.map { it.toDomain() })
            }
        } catch (e: Exception) {
            e.toTmdbResult()
        }
    }
}
