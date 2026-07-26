package com.cinemateca.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "watchlist_movies")
data class WatchlistMovieEntity(
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    val movieId: String,
)
