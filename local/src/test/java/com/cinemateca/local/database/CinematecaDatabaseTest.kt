package com.cinemateca.local.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class CinematecaDatabaseTest {
    private lateinit var database: CinematecaDatabase

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            CinematecaDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `keeps favorites and watchlist isolated in memory`() =
        runTest {
            database.favoriteMovieDao().insert(
                FavoriteMovieEntity(movieId = "favorite"),
            )
            database.watchlistMovieDao().insert(
                WatchlistMovieEntity(movieId = "watchlist"),
            )

            assertTrue(
                database.favoriteMovieDao().contains("favorite"),
            )
            assertFalse(
                database.favoriteMovieDao().contains("watchlist"),
            )
            assertTrue(
                database.watchlistMovieDao().contains("watchlist"),
            )
            assertFalse(
                database.watchlistMovieDao().contains("favorite"),
            )
        }
}
