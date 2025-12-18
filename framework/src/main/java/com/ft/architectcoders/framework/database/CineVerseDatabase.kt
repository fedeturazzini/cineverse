package com.ft.architectcoders.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [DbMovie::class, DbProfile::class],
    version = 1,
    exportSchema = false,
)
abstract class CineVerseDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val profileDao: ProfileDao
}
