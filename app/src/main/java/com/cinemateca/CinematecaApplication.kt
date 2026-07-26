package com.cinemateca

import android.app.Application
import com.cinemateca.connectivity.di.connectivityModule
import com.cinemateca.di.useCaseModule
import com.cinemateca.features.di.featuresModule
import com.cinemateca.local.di.movieSelectionLocalModule
import com.cinemateca.networking.di.kinoCheckNetworkingModule
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin

class CinematecaApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@CinematecaApplication)
            modules(
                connectivityModule,
                movieSelectionLocalModule,
                kinoCheckNetworkingModule(),
                useCaseModule,
                featuresModule,
            )
        }
    }
}
