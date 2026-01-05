package com.ft.architectcoders.di

import com.ft.architectcoders.data.di.dataModule
import com.ft.architectcoders.framework.di.frameworkModules
import com.ft.architectcoders.usecases.di.useCasesModule

val appModules =
    listOf(
        configModule,
        viewModelModule,
    ) + frameworkModules +
        listOf(
            dataModule,
            useCasesModule,
        )
