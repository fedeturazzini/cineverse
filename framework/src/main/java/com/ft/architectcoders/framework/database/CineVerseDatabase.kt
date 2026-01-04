package com.ft.architectcoders.framework.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        DbMovie::class,
        DbProfile::class,
        DbDuelSession::class,
        DbDuelChoice::class,
        DbTasteFingerprint::class,
    ],
    version = 2,
    exportSchema = false,
)
abstract class CineVerseDatabase : RoomDatabase() {
    abstract val moviesDao: MoviesDao
    abstract val profileDao: ProfileDao
    abstract val duelDao: DuelDao
}
