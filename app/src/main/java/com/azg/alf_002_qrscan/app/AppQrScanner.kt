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
        SharedPreference.Companion.init(this@AppQrScanner)
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

val isAppDebug = BuildConfig.build_debug == true
val sharedPreference get() = SharedPreference.Companion.getInstance()
val remoteConfig get() = RemoteConfig.Companion.getInstance()