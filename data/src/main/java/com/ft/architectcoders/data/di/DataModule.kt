package com.ft.architectcoders.data.di

import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.gemini.GeminiRepositoryImpl
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.data.repository.profile.ProfileRepositoryImpl
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.data.repository.region.RegionRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule = module {
    singleOf(::RegionRepositoryImpl) { bind<RegionRepository>() }
    singleOf(::MovieRepositoryImpl) { bind<MovieRepository>() }
    singleOf(::GeminiRepositoryImpl) { bind<GeminiRepository>() }
    singleOf(::ProfileRepositoryImpl) { bind<ProfileRepository>() }
}
