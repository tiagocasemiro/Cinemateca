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
    private lateinit var context: Context
    private lateinit var database: CinematecaDatabase

    @Before
    fun setUp() {
        context = ApplicationProvider.getApplicationContext()
        context.deleteDatabase(TEST_DATABASE_NAME)
        database = createDatabase()
    }

    @After
    fun tearDown() {
        if (database.isOpen) {
            database.close()
        }
        context.deleteDatabase(TEST_DATABASE_NAME)
    }

    @Test
    fun `preserves favorites and watchlist after database is reopened`() =
        runTest {
            database.favoriteMovieDao().insert(
                FavoriteMovieEntity(movieId = "favorite"),
            )
            database.watchlistMovieDao().insert(
                WatchlistMovieEntity(movieId = "watchlist"),
            )
            database.close()

            database = createDatabase()

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

    private fun createDatabase(): CinematecaDatabase {
        return Room.databaseBuilder(
            context,
            CinematecaDatabase::class.java,
            TEST_DATABASE_NAME,
        ).build()
    }

    private companion object {
        const val TEST_DATABASE_NAME = "cinemateca-test.db"
    }
}
