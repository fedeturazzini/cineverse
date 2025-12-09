package com.ft.architectcoders.data.datasource.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ft.architectcoders.domain.model.Movie
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {
    @Query("SELECT * FROM Movie")
    fun fetchMovies(): Flow<List<Movie>>

    @Query("SELECT * FROM Movie WHERE id = :id")
    fun findMovieById(id: Int): Flow<Movie>

    @Query("SELECT COUNT(*) FROM Movie")
    suspend fun countMovies(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: List<Movie>)

    @Query("DELETE FROM Movie")
    suspend fun clearMovies()

    @Query("UPDATE Movie SET aiRating = :rating, aiQuote = :quote WHERE id = :movieId")
    suspend fun updateAiReview(
        movieId: Int,
        rating: Float,
        quote: String,
    )
}
