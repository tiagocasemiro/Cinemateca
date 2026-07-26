package com.cinemateca.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteMovieDao {
    @Query("SELECT movie_id FROM favorite_movies")
    fun observeMovieIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: FavoriteMovieEntity)

    @Query("DELETE FROM favorite_movies WHERE movie_id = :movieId")
    suspend fun delete(movieId: String)

    @Query(
        "SELECT EXISTS(" +
            "SELECT 1 FROM favorite_movies WHERE movie_id = :movieId" +
            ")",
    )
    suspend fun contains(movieId: String): Boolean
}
