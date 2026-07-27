package com.cinemateca.local.database

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.cinemateca.local.adapter.FavoriteMovieLocalImpl
import com.cinemateca.local.adapter.WatchlistMovieLocalImpl
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class MovieSelectionRoomIntegrationTest {
    private lateinit var database: CinematecaDatabase
    private lateinit var favoriteDao: FavoriteMovieDao
    private lateinit var watchlistDao: WatchlistMovieDao
    private lateinit var favoriteRepository: FavoriteMovieLocalImpl
    private lateinit var watchlistRepository: WatchlistMovieLocalImpl

    @Before
    fun setUp() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        database = Room.inMemoryDatabaseBuilder(
            context,
            CinematecaDatabase::class.java,
        )
            .allowMainThreadQueries()
            .build()

        favoriteDao = database.favoriteMovieDao()
        watchlistDao = database.watchlistMovieDao()
        favoriteRepository = FavoriteMovieLocalImpl(favoriteDao)
        watchlistRepository = WatchlistMovieLocalImpl(watchlistDao)
    }

    @After
    fun tearDown() {
        database.close()
    }

    @Test
    fun `new in-memory database starts with both tables empty`() =
        runTest {
            assertEquals(emptyList<String>(), favoriteDao.observeMovieIds().first())
            assertEquals(emptyList<String>(), watchlistDao.observeMovieIds().first())
            assertFalse(favoriteDao.contains("movie"))
            assertFalse(watchlistDao.contains("movie"))
        }

    @Test
    fun `favorite DAO inserts and queries all movie ids`() =
        runTest {
            favoriteDao.insert(FavoriteMovieEntity(movieId = "movie-1"))
            favoriteDao.insert(FavoriteMovieEntity(movieId = "movie-2"))

            assertTrue(favoriteDao.contains("movie-1"))
            assertTrue(favoriteDao.contains("movie-2"))
            assertEquals(
                setOf("movie-1", "movie-2"),
                favoriteDao.observeMovieIds().first().toSet(),
            )
        }

    @Test
    fun `watchlist DAO inserts and queries all movie ids`() =
        runTest {
            watchlistDao.insert(WatchlistMovieEntity(movieId = "movie-1"))
            watchlistDao.insert(WatchlistMovieEntity(movieId = "movie-2"))

            assertTrue(watchlistDao.contains("movie-1"))
            assertTrue(watchlistDao.contains("movie-2"))
            assertEquals(
                setOf("movie-1", "movie-2"),
                watchlistDao.observeMovieIds().first().toSet(),
            )
        }

    @Test
    fun `favorite DAO ignores duplicate primary keys`() =
        runTest {
            favoriteDao.insert(FavoriteMovieEntity(movieId = "duplicate"))
            favoriteDao.insert(FavoriteMovieEntity(movieId = "duplicate"))

            assertEquals(
                listOf("duplicate"),
                favoriteDao.observeMovieIds().first(),
            )
        }

    @Test
    fun `watchlist DAO ignores duplicate primary keys`() =
        runTest {
            watchlistDao.insert(WatchlistMovieEntity(movieId = "duplicate"))
            watchlistDao.insert(WatchlistMovieEntity(movieId = "duplicate"))

            assertEquals(
                listOf("duplicate"),
                watchlistDao.observeMovieIds().first(),
            )
        }

    @Test
    fun `favorite DAO deletes only the requested movie`() =
        runTest {
            favoriteDao.insert(FavoriteMovieEntity(movieId = "keep"))
            favoriteDao.insert(FavoriteMovieEntity(movieId = "delete"))

            favoriteDao.delete("delete")

            assertTrue(favoriteDao.contains("keep"))
            assertFalse(favoriteDao.contains("delete"))
            assertEquals(setOf("keep"), favoriteDao.observeMovieIds().first().toSet())
        }

    @Test
    fun `watchlist DAO deletes only the requested movie`() =
        runTest {
            watchlistDao.insert(WatchlistMovieEntity(movieId = "keep"))
            watchlistDao.insert(WatchlistMovieEntity(movieId = "delete"))

            watchlistDao.delete("delete")

            assertTrue(watchlistDao.contains("keep"))
            assertFalse(watchlistDao.contains("delete"))
            assertEquals(setOf("keep"), watchlistDao.observeMovieIds().first().toSet())
        }

    @Test
    fun `deleting an unknown id keeps favorite table unchanged`() =
        runTest {
            favoriteDao.insert(FavoriteMovieEntity(movieId = "existing"))

            favoriteDao.delete("unknown")

            assertEquals(setOf("existing"), favoriteDao.observeMovieIds().first().toSet())
        }

    @Test
    fun `deleting an unknown id keeps watchlist table unchanged`() =
        runTest {
            watchlistDao.insert(WatchlistMovieEntity(movieId = "existing"))

            watchlistDao.delete("unknown")

            assertEquals(setOf("existing"), watchlistDao.observeMovieIds().first().toSet())
        }

    @Test
    fun `favorite repository inserts and deletes through Room`() =
        runTest {
            favoriteRepository.setSelected(movieId = "movie-1", isSelected = true)
            favoriteRepository.setSelected(movieId = "movie-2", isSelected = true)

            assertEquals(
                setOf("movie-1", "movie-2"),
                favoriteRepository.observeMovieIds().first(),
            )
            assertTrue(favoriteDao.contains("movie-1"))

            favoriteRepository.setSelected(movieId = "movie-1", isSelected = false)

            assertEquals(
                setOf("movie-2"),
                favoriteRepository.observeMovieIds().first(),
            )
            assertFalse(favoriteDao.contains("movie-1"))
        }

    @Test
    fun `watchlist repository inserts and deletes through Room`() =
        runTest {
            watchlistRepository.setSelected(movieId = "movie-1", isSelected = true)
            watchlistRepository.setSelected(movieId = "movie-2", isSelected = true)

            assertEquals(
                setOf("movie-1", "movie-2"),
                watchlistRepository.observeMovieIds().first(),
            )
            assertTrue(watchlistDao.contains("movie-1"))

            watchlistRepository.setSelected(movieId = "movie-1", isSelected = false)

            assertEquals(
                setOf("movie-2"),
                watchlistRepository.observeMovieIds().first(),
            )
            assertFalse(watchlistDao.contains("movie-1"))
        }

    @Test
    fun `repository repeated selection remains idempotent`() =
        runTest {
            favoriteRepository.setSelected(movieId = "movie", isSelected = true)
            favoriteRepository.setSelected(movieId = "movie", isSelected = true)
            watchlistRepository.setSelected(movieId = "movie", isSelected = true)
            watchlistRepository.setSelected(movieId = "movie", isSelected = true)

            assertEquals(setOf("movie"), favoriteRepository.observeMovieIds().first())
            assertEquals(setOf("movie"), watchlistRepository.observeMovieIds().first())
        }

    @Test
    fun `favorite and watchlist tables remain isolated`() =
        runTest {
            favoriteRepository.setSelected(movieId = "favorite-only", isSelected = true)
            watchlistRepository.setSelected(movieId = "watchlist-only", isSelected = true)
            favoriteRepository.setSelected(movieId = "shared", isSelected = true)
            watchlistRepository.setSelected(movieId = "shared", isSelected = true)

            assertEquals(
                setOf("favorite-only", "shared"),
                favoriteRepository.observeMovieIds().first(),
            )
            assertEquals(
                setOf("watchlist-only", "shared"),
                watchlistRepository.observeMovieIds().first(),
            )
            assertFalse(favoriteDao.contains("watchlist-only"))
            assertFalse(watchlistDao.contains("favorite-only"))
        }
}
