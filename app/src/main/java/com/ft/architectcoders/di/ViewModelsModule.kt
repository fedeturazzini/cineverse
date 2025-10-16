package com.ft.architectcoders.di

import com.ft.architectcoders.ui.screens.detail.MovieDetailViewModel
import com.ft.architectcoders.ui.screens.home.HomeViewModel
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::HomeViewModel)

        viewModel { params ->
            MovieDetailViewModel(
                movieId = params.get(),
                movieRepository = get(),
                geminiAiServiceImpl = get(),
            )
        }
    }
