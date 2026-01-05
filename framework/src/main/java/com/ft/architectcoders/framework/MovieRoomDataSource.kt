package com.ft.architectcoders.framework

import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.domain.model.AiReview
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.framework.database.DbMovie
import com.ft.architectcoders.framework.database.MoviesDao
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.Int

class MovieRoomDataSource(
    private val moviesDao: MoviesDao,
) : MovieLocalDataSource {
    override val movies: Flow<List<Movie>> =
        moviesDao.fetchMovies().map { list -> list.map { it.toDomainMovie() } }

    override fun findMovieById(id: Int): Flow<Movie?> = moviesDao.findMovieById(id).map { it?.toDomainMovie() }

    override suspend fun countMovies(): Int = moviesDao.countMovies()

    override suspend fun saveMovies(movies: List<Movie>) {
        moviesDao.saveMovies(movies.map { it.toDbMovie() })
    }

    override suspend fun isEmpty(): Boolean = countMovies() == 0

    override suspend fun clearMovies() = moviesDao.clearMovies()

    override suspend fun updateAiReview(
        movieId: Int,
        review: AiReview,
    ) {
        moviesDao.updateAiReview(movieId, review.rating, review.quote)
    }

    private fun DbMovie.toDomainMovie(): Movie =
        Movie(
            id,
            title,
            originalTitle,
            poster,
            backdrop,
            releaseDate,
            overview,
            favorite,
            aiRating,
            aiQuote,
        )

    private fun Movie.toDbMovie() =
        DbMovie(
            id,
            title,
            originalTitle,
            poster,
            backdrop,
            releaseDate,
            overview,
            favorite,
            aiRating,
            aiQuote,
        )
}
