package com.azg.alf_002_qrscan.app

import android.app.Application
import com.azg.alf_002_qrscan.BuildConfig
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class AppQrScanner : Application() {
    companion object {
        var isAppForeground = false
    }
    override fun onCreate() {
        super.onCreate()
        startKoin {
            androidLogger()
            androidContext(this@AppQrScanner)
            modules(
                listOf(
                    dataModule,
                    viewModelModule,
                )
            )
        }

    }
}