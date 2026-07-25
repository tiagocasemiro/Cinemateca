package com.cinemateca

import android.app.Application
import com.cinemateca.networking.di.kinoCheckNetworkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CinematecaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CinematecaApplication)
            modules(kinoCheckNetworkingModule())
        }
    }
}
