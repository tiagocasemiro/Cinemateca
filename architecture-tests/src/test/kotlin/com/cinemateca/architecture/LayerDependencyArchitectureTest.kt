package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class LayerDependencyArchitectureTest {

    @Test
    fun `domain does not depend on infrastructure or UI`() {
        val domainFiles = ArchitectureScope.productionFiles.filter {
            it.moduleName == "domain"
        }

        domainFiles.assertTrue(
            strict = true,
            additionalMessage = ORIGIN,
        ) { file ->
            file.importNames.none { dependency ->
                forbiddenDomainDependencies.any(dependency::startsWith)
            }
        }
    }

    @Test
    fun `features do not depend directly on data sources`() {
        val featureFiles = ArchitectureScope.productionFiles.filter {
            it.moduleName == "features"
        }

        featureFiles.assertTrue(
            strict = true,
            additionalMessage = ORIGIN,
        ) { file ->
            file.importNames.none { dependency ->
                forbiddenFeatureDependencies.any(dependency::startsWith)
            }
        }
    }

    private companion object {
        const val ORIGIN =
            "Expected dependency direction UI -> ViewModel -> UseCase -> Repository. " +
                "Source: references/overview.md, sections Camadas and Fluxo simplificado."

        val forbiddenDomainDependencies = listOf(
            "android.",
            "androidx.",
            "com.cinemateca.features.",
            "com.cinemateca.local.",
            "com.cinemateca.networking.",
            "retrofit2.",
            "okhttp3.",
            "androidx.room.",
        )

        val forbiddenFeatureDependencies = listOf(
            "com.cinemateca.local.",
            "com.cinemateca.networking.",
            "retrofit2.",
            "okhttp3.",
            "androidx.room.",
        )
    }
}
