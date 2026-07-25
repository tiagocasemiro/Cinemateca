package com.cinemateca.connectivity.di

import android.content.Context
import android.net.ConnectivityManager
import com.cinemateca.connectivity.adapter.AndroidInternetConnectionLocalImpl
import com.cinemateca.domain.connectivity.repository.InternetConnectionRepository
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val connectivityModule = module {
    single<InternetConnectionRepository.Local> {
        AndroidInternetConnectionLocalImpl(
            connectivityManager = androidContext().getSystemService(
                Context.CONNECTIVITY_SERVICE,
            ) as ConnectivityManager,
        )
    }
}
