package com.ft.architectcoders.di

import android.location.Geocoder
import com.ft.architectcoders.BuildConfig.*
import com.ft.architectcoders.data.datasource.LocationDataSource
import com.ft.architectcoders.data.datasource.MovieLocalDataSource
import com.ft.architectcoders.data.datasource.MovieRemoteDataSource
import com.ft.architectcoders.data.datasource.ProfileLocalDataSource
import com.ft.architectcoders.data.datasource.GeminiAiService
import com.ft.architectcoders.data.datasource.RegionDataSource
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.gemini.GeminiRepositoryImpl
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.data.repository.profile.ProfileRepositoryImpl
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.data.repository.region.RegionRepositoryImpl
import com.ft.architectcoders.usecases.FetchMovieUseCaseImpl
import com.ft.architectcoders.usecases.FetchMoviesUseCase
import com.ft.architectcoders.usecases.FindMovieByIdUseCase
import com.ft.architectcoders.usecases.FindMovieByIdUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieCreditsUseCase
import com.ft.architectcoders.usecases.GetMovieCreditsUseCaseImpl
import com.ft.architectcoders.usecases.GetMovieVideosUseCase
import com.ft.architectcoders.usecases.GetMovieVideosUseCaseImpl
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCase
import com.ft.architectcoders.usecases.ToggleFavoriteMovieUseCaseImpl
import com.ft.architectcoders.framework.MovieRoomDataSource
import com.ft.architectcoders.framework.MovieServerDataSourceImpl
import com.ft.architectcoders.framework.RegionDataSourceImpl
import com.ft.architectcoders.framework.LocationDataSourceImpl
import com.ft.architectcoders.framework.ProfileLocalDataSourceImpl
import com.ft.architectcoders.framework.remote.tmdb.TmdbService
import com.ft.architectcoders.framework.remote.tmdb.TmdbApiClient
import com.ft.architectcoders.framework.remote.gemini.GeminiAiServiceImpl
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module

val dataModule =
    module {
        single<TmdbService> {
            TmdbApiClient.build(apiKey = TMDB_API_KEY)
        }

        single<MovieRemoteDataSource> {
            MovieServerDataSourceImpl(
                tmdbService = get(),
                regionRepository = get(),
            )
        }

        single<MovieLocalDataSource> {
            MovieRoomDataSource(moviesDao = get())
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

        single<Geocoder> {
            Geocoder(androidApplication())
        }

        single<RegionDataSource> {
            RegionDataSourceImpl(
                geocoder = get(),
                locationDataSource = get(),
            )
        }

        single<FusedLocationProviderClient> {
            LocationServices.getFusedLocationProviderClient(androidApplication())
        }

        single<LocationDataSource> {
            LocationDataSourceImpl(fusedLocationClient = get())
        }

        single<GeminiAiService> {
            GeminiAiServiceImpl(apiKey = GEMINI_API_KEY)
        }

        single<GeminiRepository> {
            GeminiRepositoryImpl(
                geminiAiService = get(),
                localDataSource = get(),
            )
        }

        single<ProfileLocalDataSource> {
            ProfileLocalDataSourceImpl(profileDao = get())
        }

        single<ProfileRepository> {
            ProfileRepositoryImpl(localDataSource = get())
        }

        // Use Cases
        single<FetchMoviesUseCase> {
            FetchMovieUseCaseImpl(movieRepository = get())
        }

        single<FindMovieByIdUseCase> {
            FindMovieByIdUseCaseImpl(movieRepository = get())
        }

        single<GetMovieCreditsUseCase> {
            GetMovieCreditsUseCaseImpl(movieRepository = get())
        }

        single<GetMovieVideosUseCase> {
            GetMovieVideosUseCaseImpl(movieRepository = get())
        }

        single<ToggleFavoriteMovieUseCase> {
            ToggleFavoriteMovieUseCaseImpl(movieRepository = get())
        }
    }
