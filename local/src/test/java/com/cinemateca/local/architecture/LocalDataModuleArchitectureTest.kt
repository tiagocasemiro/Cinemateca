package com.cinemateca.local.architecture

import java.io.File
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class LocalDataModuleArchitectureTest {
    @Test
    fun `app does not own Room declarations`() {
        val appSources = projectRoot()
            .resolve("app/src/main")
            .walkTopDown()
            .filter(File::isFile)
            .filter { it.extension == "kt" }
            .toList()

        assertTrue("Expected Kotlin production sources in the app module", appSources.isNotEmpty())

        val violations = appSources.filter { source ->
            val content = source.readText()
            content.contains("import androidx.room.") ||
                content.contains("@Dao") ||
                content.contains("@Database") ||
                content.contains("@Entity")
        }

        assertFalse(
            "Room belongs to the local module. Violations: ${violations.joinToString()}",
            violations.isNotEmpty(),
        )
    }

    @Test
    fun `local module owns the Room database and DAOs`() {
        val databasePackage = projectRoot()
            .resolve("local/src/main/java/com/cinemateca/local/database")

        assertTrue(databasePackage.resolve("CinematecaDatabase.kt").isFile)
        assertTrue(databasePackage.resolve("FavoriteMovieDao.kt").isFile)
        assertTrue(databasePackage.resolve("WatchlistMovieDao.kt").isFile)
    }

    private fun projectRoot(): File {
        val workingDirectory = requireNotNull(System.getProperty("user.dir"))

        return generateSequence(File(workingDirectory).absoluteFile) {
            it.parentFile
        }.first { candidate ->
            candidate.resolve("settings.gradle.kts").isFile
        }
    }
}
