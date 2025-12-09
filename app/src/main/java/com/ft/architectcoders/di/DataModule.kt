package com.ft.architectcoders.di

import com.ft.architectcoders.BuildConfig
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.ft.architectcoders.data.datasource.LocationDataSourceImpl
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSourceImpl
import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.data.datasource.RegionDataSource
import com.ft.architectcoders.data.datasource.RegionDataSourceImpl
import com.ft.architectcoders.data.datasource.remote.gemini.GeminiAiService
import com.ft.architectcoders.data.datasource.remote.gemini.GeminiAiServiceImpl
import com.ft.architectcoders.data.datasource.remote.tmdb.TmdbApiClient
import com.ft.architectcoders.data.datasource.remote.tmdb.TmdbService
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.gemini.GeminiRepositoryImpl
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.data.repository.profile.ProfileRepositoryImpl
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.data.repository.region.RegionRepositoryImpl
import org.koin.dsl.module

val dataModule =
    module {

        single<TmdbService> {
            TmdbApiClient.build(apiKey = BuildConfig.TMDB_API_KEY)
        }

        single<MovieRemoteDataSource> {
            MovieRemoteDataSourceImpl(
                tmdbService = get(),
                regionRepository = get(),
            )
        }

        single<MovieLocalDataSource> {
            MovieLocalDataSource(moviesDao = get())
        }

        single<MovieRepository> {
            MovieRepositoryImpl(
                remoteDataSource = get(),
                localDataSource = get(),
            )
        }

        single<RegionRepository> {
            RegionRepositoryImpl(regionDataSource = get())
        }

        single<RegionDataSource> {
            RegionDataSourceImpl(
                application = get(),
                locationDataSource = get(),
            )
        }

        single<LocationDataSource> {
            LocationDataSourceImpl(app = get())
        }

        single<GeminiAiService> {
            GeminiAiServiceImpl(apiKey = BuildConfig.GEMINI_API_KEY)
        }

        single<GeminiRepository> {
            GeminiRepositoryImpl(
                geminiAiService = get(),
                localDataSource = get(),
            )
        }

        single<ProfileLocalDataSource> {
            ProfileLocalDataSource(profileDao = get())
        }

        single<ProfileRepository> {
            ProfileRepositoryImpl(localDataSource = get())
        }
    }
