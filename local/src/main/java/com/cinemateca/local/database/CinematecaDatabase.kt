package com.cinemateca.local.database

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        FavoriteMovieEntity::class,
        WatchlistMovieEntity::class,
    ],
    version = 1,
    exportSchema = false,
)
abstract class CinematecaDatabase : RoomDatabase() {
    abstract fun favoriteMovieDao(): FavoriteMovieDao

    abstract fun watchlistMovieDao(): WatchlistMovieDao
}
