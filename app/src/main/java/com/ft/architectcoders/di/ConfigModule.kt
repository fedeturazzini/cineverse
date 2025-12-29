package com.ft.architectcoders.di

import com.ft.architectcoders.BuildConfig.GEMINI_API_KEY
import com.ft.architectcoders.BuildConfig.TMDB_API_KEY
import com.ft.architectcoders.framework.di.GEMINI_API_KEY as GEMINI_API_KEY_QUALIFIER
import com.ft.architectcoders.framework.di.TMDB_API_KEY as TMDB_API_KEY_QUALIFIER
import org.koin.dsl.module

val configModule = module {
    single(TMDB_API_KEY_QUALIFIER) { TMDB_API_KEY }
    single(GEMINI_API_KEY_QUALIFIER) { GEMINI_API_KEY }
}

