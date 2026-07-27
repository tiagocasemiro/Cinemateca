package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Test

class ReusableAssetsArchitectureTest {

    @Test
    fun `domain has one canonical Result hierarchy`() {
        val resultFiles = ArchitectureScope.productionFiles.filter { file ->
            file.classes(includeNested = false, includeLocal = false)
                .any { it.name == "Result" }
        }

        assertEquals(RESULT_ORIGIN, 1, resultFiles.size)
        resultFiles.assertTrue(
            strict = true,
            additionalMessage = RESULT_ORIGIN,
        ) { file ->
            file.moduleName == "domain" &&
                file.packageName == "com.cinemateca.domain" &&
                requiredResultDeclarations.all(file.text::contains)
        }
    }

    @Test
    fun `repository mapping and remote execution assets are canonical`() {
        val infrastructureFiles = ArchitectureScope.productionFiles.filter {
            it.packageName == "com.cinemateca.repository"
        }

        infrastructureFiles.assertTrue(
            strict = true,
            additionalMessage = REPOSITORY_ASSETS_ORIGIN,
        ) { file ->
            file.moduleName == "networking"
        }

        val declarationNames = infrastructureFiles.flatMap { file ->
            file.interfaces(includeNested = false).map { it.name } +
                file.functions(includeNested = false, includeLocal = false).map { it.name }
        }
        requiredRepositoryDeclarations.forEach { declaration ->
            assertEquals(
                "$REPOSITORY_ASSETS_ORIGIN Missing or duplicated declaration: $declaration",
                1,
                declarationNames.count { it == declaration },
            )
        }
    }

    @Test
    fun `fetchData owns the IO dispatcher boundary`() {
        val fetchDataFunctions = ArchitectureScope.productionFiles
            .flatMap { it.functions(includeNested = false, includeLocal = false) }
            .filter { it.name == "fetchData" }

        fetchDataFunctions.assertTrue(
            strict = true,
            additionalMessage = FETCH_DATA_ORIGIN,
        ) { function ->
            function.hasSuspendModifier &&
                function.moduleName == "networking" &&
                "withContext(Dispatchers.IO)" in function.text &&
                "ConnectException" in function.text
        }
    }

    private companion object {
        val requiredResultDeclarations = listOf(
            "sealed class Result",
            "data class Success",
            "data class Failure",
            "data class Loading",
            "data class Error",
        )

        val requiredRepositoryDeclarations = listOf(
            "DomainMapperResponse",
            "fetchData",
            "extractData",
            "extractList",
            "extractNoData",
            "processData",
        )

        const val RESULT_ORIGIN =
            "Expected one canonical domain Result hierarchy. " +
                "Source: references/repository.md and references/use-case.md, Uso dos assets; " +
                "asset: assets/usecase/domain/Result.kt."
        const val REPOSITORY_ASSETS_ORIGIN =
            "Expected canonical Repository assets in networking infrastructure. " +
                "Source: references/repository.md, section Uso dos assets; " +
                "assets: DomainMapper.kt, FetchData.kt and NetworkResult.kt."
        const val FETCH_DATA_ORIGIN =
            "Expected fetchData to own IO execution and connection exception translation. " +
                "Source: references/repository.md, section Executar a chamada com fetchData; " +
                "asset: assets/repository/FetchData.kt."
    }
}
