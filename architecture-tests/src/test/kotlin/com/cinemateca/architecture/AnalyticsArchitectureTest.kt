package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class AnalyticsArchitectureTest {

    @Test
    fun `analytics implementation details remain internal`() {
        val implementationTypes = ArchitectureScope.productionFiles
            .filter { it.packageName.startsWith("com.cinemateca.analytics") }
            .flatMap { file ->
                file.classes(includeNested = false, includeLocal = false) +
                    file.interfaces(includeNested = false)
            }
            .filter { it.name != "AppAnalytics" && it.name !in publicModels }

        implementationTypes.assertTrue(
            additionalMessage = INTERNAL_ORIGIN,
        ) { it.hasInternalModifier }
    }

    @Test
    fun `features know only the public analytics contract and models`() {
        val featureFiles = ArchitectureScope.productionFiles.filter {
            it.moduleName == "features"
        }

        featureFiles.assertTrue(
            strict = true,
            additionalMessage = BOUNDARY_ORIGIN,
        ) { file ->
            file.importNames
                .filter { it.startsWith("com.cinemateca.analytics.") }
                .all { dependency ->
                    dependency == "com.cinemateca.analytics.AppAnalytics" ||
                        dependency.startsWith("com.cinemateca.analytics.events.")
                }
        }
    }

    private companion object {
        val publicModels = setOf(
            "AnalyticsEvent",
            "Event",
            "AnalyticsIdentification",
            "Identification",
        )

        const val INTERNAL_ORIGIN =
            "Expected managers, trackers and provider details to remain internal. " +
                "Source: references/analytics.md, opening contract and Checklist de Analytics."
        const val BOUNDARY_ORIGIN =
            "Expected features to know only AppAnalytics and event/identification models. " +
                "Source: references/analytics.md, sections Contrato público and Dependências."
    }
}
