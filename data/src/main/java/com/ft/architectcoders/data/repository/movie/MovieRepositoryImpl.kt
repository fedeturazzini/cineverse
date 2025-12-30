package com.ft.architectcoders.data.repository.movie

import com.ft.architectcoders.domain.Result
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.domain.model.Cast
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.MovieVideo
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.onEach

class MovieRepositoryImpl(
    private val remoteDataSource: MovieRemoteDataSource,
    private val localDataSource: MovieLocalDataSource,
) : MovieRepository {
    override val movies: Flow<List<Movie>> get() =
        localDataSource.movies.onEach { localMovies ->
            if (localMovies.isEmpty()) {
                when (val result = remoteDataSource.fetchPopularMovies()) {
                    is Result.Success -> localDataSource.saveMovies(result.data)
                    is Result.Error -> {}
                    is Result.Loading -> {}
                }
            }
        }

    override fun findMovieById(id: Int): Flow<Result<Movie>> =
        flow {
            val localMovie = localDataSource.findMovieById(id).firstOrNull()

            if (localMovie != null) {
                localDataSource.findMovieById(id).collect { movie ->
                    emit(Result.Success(movie))
                }
            } else {
                when (val remoteResult = remoteDataSource.findMovieById(id)) {
                    is Result.Success -> {
                        localDataSource.saveMovies(listOf(remoteResult.data))
                        localDataSource.findMovieById(id).collect { movie ->
                            emit(Result.Success(movie))
                        }
                    }
                    is Result.Error -> emit(remoteResult)
                    is Result.Loading -> emit(remoteResult)
                }
            }
        }

    override fun getMovieCredits(movieId: Int): Flow<Result<List<Cast>>> =
        flow {
            emit(remoteDataSource.fetchMovieCredits(movieId))
        }

    override suspend fun toggleFavorite(movie: Movie) {
        localDataSource.saveMovies(
            listOf(movie.copy(favorite = !movie.favorite)),
        )
    }

    override fun getMovieVideos(movieId: Int): Flow<Result<List<MovieVideo>>> =
        flow {
            emit(remoteDataSource.fetchMovieVideos(movieId))
        }

    override suspend fun searchMovies(query: String): List<Movie> {
        return when (val result = remoteDataSource.searchMovies(query)) {
            is Result.Success -> result.data
            else -> emptyList()
        }
    }
}
