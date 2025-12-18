package com.ft.architectcoders.di

import android.app.Application
import androidx.room.Room
import com.ft.architectcoders.framework.database.CineVerseDatabase
import com.ft.architectcoders.framework.database.MoviesDao
import com.ft.architectcoders.framework.database.ProfileDao
import org.koin.dsl.module

val databaseModule =
    module {
        single<CineVerseDatabase> {
            Room.databaseBuilder(
                get<Application>(),
                CineVerseDatabase::class.java,
                "cineverse_database",
            ).build()
        }

        single<MoviesDao> {
            get<CineVerseDatabase>().moviesDao
        }

        single<ProfileDao> {
            get<CineVerseDatabase>().profileDao
        }
    }
