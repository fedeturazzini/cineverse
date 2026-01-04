package com.ft.architectcoders.di

import com.ft.architectcoders.ui.screens.detail.MovieDetailViewModel
import com.ft.architectcoders.ui.screens.duel.DuelViewModel
import com.ft.architectcoders.ui.screens.home.HomeViewModel
import com.ft.architectcoders.ui.screens.profile.ProfileViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val viewModelModule =
    module {
        viewModelOf(::HomeViewModel)

        viewModelOf(::MovieDetailViewModel)

        viewModelOf(::ProfileViewModel)

        viewModelOf(::DuelViewModel)
    }
