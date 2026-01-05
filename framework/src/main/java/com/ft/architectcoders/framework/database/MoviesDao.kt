package com.ft.architectcoders.framework.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MoviesDao {
    @Query("SELECT * FROM DbMovie")
    fun fetchMovies(): Flow<List<DbMovie>>

    @Query("SELECT * FROM DbMovie WHERE id = :id")
    fun findMovieById(id: Int): Flow<DbMovie?>

    @Query("SELECT COUNT(*) FROM DbMovie")
    suspend fun countMovies(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveMovies(movies: List<DbMovie>)

    @Query("DELETE FROM DbMovie")
    suspend fun clearMovies()

    @Query("UPDATE DbMovie SET aiRating = :rating, aiQuote = :quote WHERE id = :movieId")
    suspend fun updateAiReview(
        movieId: Int,
        rating: Float,
        quote: String,
    )
}
