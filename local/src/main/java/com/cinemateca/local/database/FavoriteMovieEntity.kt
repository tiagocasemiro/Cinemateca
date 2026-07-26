package com.cinemateca.local.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_movies")
data class FavoriteMovieEntity(
    @PrimaryKey
    @ColumnInfo(name = "movie_id")
    val movieId: String,
)
