package com.azg.sample.app

import android.app.Application
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppProjectApplication : Application() {
    companion object {
        var isAppForeground = false
    }
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AppProjectApplication)
            modules(
                listOf(
                    dataModule,
                    viewModelModule,
                )
            )
        }

    }
}