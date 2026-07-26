package com.cinemateca.local.di

import androidx.room.Room
import com.cinemateca.domain.movies.repository.FavoriteMovieRepository
import com.cinemateca.domain.movies.repository.WatchlistMovieRepository
import com.cinemateca.local.adapter.FavoriteMovieLocalImpl
import com.cinemateca.local.adapter.WatchlistMovieLocalImpl
import com.cinemateca.local.database.CinematecaDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

private const val DATABASE_NAME = "cinemateca.db"

val movieSelectionLocalModule = module {
    single {
        Room.databaseBuilder(
            androidContext(),
            CinematecaDatabase::class.java,
            DATABASE_NAME,
        ).build()
    }
    single {
        get<CinematecaDatabase>().favoriteMovieDao()
    }
    single {
        get<CinematecaDatabase>().watchlistMovieDao()
    }
    single<FavoriteMovieRepository.Local> {
        FavoriteMovieLocalImpl(dao = get())
    }
    single<WatchlistMovieRepository.Local> {
        WatchlistMovieLocalImpl(dao = get())
    }
}
