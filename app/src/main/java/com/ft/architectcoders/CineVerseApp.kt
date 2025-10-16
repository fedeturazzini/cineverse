package com.ft.architectcoders

import android.app.Application
import com.ft.architectcoders.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CineVerseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidContext(this@CineVerseApp)
            modules(appModules)
        }
    }
}
