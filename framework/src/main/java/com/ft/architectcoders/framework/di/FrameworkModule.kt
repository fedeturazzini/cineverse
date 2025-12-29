package com.ft.architectcoders.framework.di

import android.location.Geocoder
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.data.datasource.RegionDataSource
import com.ft.architectcoders.framework.LocationDataSourceImpl
import com.ft.architectcoders.framework.MovieRoomDataSource
import com.ft.architectcoders.framework.MovieRemoteDataSourceImpl
import com.ft.architectcoders.framework.ProfileLocalDataSourceImpl
import com.ft.architectcoders.framework.RegionDataSourceImpl
import com.ft.architectcoders.framework.remote.gemini.GeminiAiServiceImpl
import com.ft.architectcoders.framework.remote.tmdb.TmdbApiClient
import com.ft.architectcoders.framework.remote.tmdb.TmdbService
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.core.qualifier.named
import org.koin.dsl.module

val TMDB_API_KEY = named("TMDB_API_KEY")
val GEMINI_API_KEY = named("GEMINI_API_KEY")

val frameworkModule = module {
    single<TmdbService> {
        TmdbApiClient.build(apiKey = get(TMDB_API_KEY))
    }

    single<FusedLocationProviderClient> {
        LocationServices.getFusedLocationProviderClient(androidApplication())
    }

    single<Geocoder> {
        Geocoder(androidApplication())
    }

    single<GeminiAiService> {
        GeminiAiServiceImpl(apiKey = get(GEMINI_API_KEY))
    }

    singleOf(::LocationDataSourceImpl) { bind<LocationDataSource>() }
    singleOf(::RegionDataSourceImpl) { bind<RegionDataSource>() }
    singleOf(::MovieRemoteDataSourceImpl) { bind<MovieRemoteDataSource>() }
    singleOf(::MovieRoomDataSource) { bind<MovieLocalDataSource>() }
    singleOf(::ProfileLocalDataSourceImpl) { bind<ProfileLocalDataSource>() }
}

val frameworkModules = listOf(databaseModule, frameworkModule)
