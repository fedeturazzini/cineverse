package com.ft.architectcoders.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.ft.architectcoders.domain.model.Profile

@Database(
    entities = [DbMovie::class, Profile::class],
    version = 1,
    exportSchema = false,
)
abstract class CineVerseDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val profileDao: ProfileDao
}
