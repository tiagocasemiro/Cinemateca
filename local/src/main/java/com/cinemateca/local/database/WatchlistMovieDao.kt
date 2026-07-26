package com.cinemateca.local.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WatchlistMovieDao {
    @Query("SELECT movie_id FROM watchlist_movies")
    fun observeMovieIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(movie: WatchlistMovieEntity)

    @Query("DELETE FROM watchlist_movies WHERE movie_id = :movieId")
    suspend fun delete(movieId: String)

    @Query(
        "SELECT EXISTS(" +
            "SELECT 1 FROM watchlist_movies WHERE movie_id = :movieId" +
            ")",
    )
    suspend fun contains(movieId: String): Boolean
}
