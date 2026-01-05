package com.ft.architectcoders

import android.app.Application
import com.ft.architectcoders.di.appModules
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class CineVerseApp : Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@CineVerseApp)
            modules(appModules)
        }
    }
}
