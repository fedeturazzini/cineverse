package com.ft.architectcoders.data.datasource.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ft.architectcoders.domain.model.Movie
import com.ft.architectcoders.domain.model.Profile

@Database(
    entities = [Movie::class, Profile::class],
    version = 1,
    exportSchema = false)
abstract class CineVerseDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val profileDao: ProfileDao
}
