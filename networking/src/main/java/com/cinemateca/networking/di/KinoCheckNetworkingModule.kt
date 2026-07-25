package com.cinemateca.networking.di

import com.cinemateca.domain.movies.repository.MovieRepository
import com.cinemateca.domain.trailers.repository.TrailerRepository
import com.cinemateca.networking.adapter.MovieRemoteImpl
import com.cinemateca.networking.adapter.TrailerRemoteImpl
import com.cinemateca.networking.gateway.KinoCheckGateway
import com.cinemateca.networking.response.TrailerPageResponse
import com.cinemateca.networking.response.TrailerPageResponseDeserializer
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import org.koin.core.module.Module
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val KINO_CHECK_BASE_URL = "https://api.kinocheck.com/"
private const val API_HOST = "api.kinocheck.com"

fun kinoCheckNetworkingModule(apiKey: String? = null): Module = module {
    single {
        GsonBuilder()
            .registerTypeAdapter(
                TrailerPageResponse::class.java,
                TrailerPageResponseDeserializer(),
            )
            .create()
    }
    single {
        val headersInterceptor = Interceptor { chain ->
            val request = chain.request()
                .newBuilder()
                .header("Accept", "application/json")
                .apply {
                    if (!apiKey.isNullOrBlank()) {
                        header("X-Api-Key", apiKey)
                        header("X-Api-Host", API_HOST)
                    }
                }
                .build()
            chain.proceed(request)
        }

        OkHttpClient.Builder()
            .addInterceptor(headersInterceptor)
            .build()
    }
    single {
        Retrofit.Builder()
            .baseUrl(KINO_CHECK_BASE_URL)
            .client(get())
            .addConverterFactory(GsonConverterFactory.create(get()))
            .build()
    }
    single<KinoCheckGateway> {
        get<Retrofit>().create(KinoCheckGateway::class.java)
    }
    factory<TrailerRepository.Remote> {
        TrailerRemoteImpl(gateway = get())
    }
    factory<MovieRepository.Remote> {
        MovieRemoteImpl(gateway = get())
    }
}
