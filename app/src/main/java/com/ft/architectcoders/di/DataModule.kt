package com.ft.architectcoders.di

import com.ft.architectcoders.BuildConfig
import com.ft.architectcoders.data.remote.gemini.GeminiAiServiceImpl
import com.ft.architectcoders.data.remote.tmdb.TmdbApiClient
import com.ft.architectcoders.data.remote.tmdb.TmdbService
import com.ft.architectcoders.data.repository.MovieRepositoryImpl
import com.ft.architectcoders.domain.repository.MovieRepository
import org.koin.dsl.module

val dataModule =
    module {

        single<TmdbService> {
            TmdbApiClient.build(apiKey = BuildConfig.TMDB_API_KEY)
        }

        single<MovieRepository> {
            MovieRepositoryImpl(tmdbService = get())
        }

        single<GeminiAiServiceImpl> {
            GeminiAiServiceImpl(apiKey = BuildConfig.GEMINI_API_KEY)
        }
    }
