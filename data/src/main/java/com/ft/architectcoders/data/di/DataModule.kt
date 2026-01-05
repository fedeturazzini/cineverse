package com.ft.architectcoders.data.di

import com.ft.architectcoders.data.repository.duel.DuelRepository
import com.ft.architectcoders.data.repository.duel.DuelRepositoryImpl
import com.ft.architectcoders.data.repository.gemini.GeminiRepository
import com.ft.architectcoders.data.repository.gemini.GeminiRepositoryImpl
import com.ft.architectcoders.data.repository.marathon.MarathonRepository
import com.ft.architectcoders.data.repository.marathon.MarathonRepositoryImpl
import com.ft.architectcoders.data.repository.mood.MoodRepository
import com.ft.architectcoders.data.repository.mood.MoodRepositoryImpl
import com.ft.architectcoders.data.repository.movie.MovieRepository
import com.ft.architectcoders.data.repository.movie.MovieRepositoryImpl
import com.ft.architectcoders.data.repository.profile.ProfileRepository
import com.ft.architectcoders.data.repository.profile.ProfileRepositoryImpl
import com.ft.architectcoders.data.repository.region.RegionRepository
import com.ft.architectcoders.data.repository.region.RegionRepositoryImpl
import com.ft.architectcoders.data.repository.taste.TasteRepository
import com.ft.architectcoders.data.repository.taste.TasteRepositoryImpl
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val dataModule =
    module {
        singleOf(::RegionRepositoryImpl) { bind<RegionRepository>() }
        singleOf(::MovieRepositoryImpl) { bind<MovieRepository>() }
        singleOf(::GeminiRepositoryImpl) { bind<GeminiRepository>() }
        singleOf(::ProfileRepositoryImpl) { bind<ProfileRepository>() }
        singleOf(::DuelRepositoryImpl) { bind<DuelRepository>() }
        singleOf(::TasteRepositoryImpl) { bind<TasteRepository>() }
        singleOf(::MoodRepositoryImpl) { bind<MoodRepository>() }
        singleOf(::MarathonRepositoryImpl) { bind<MarathonRepository>() }
    }
