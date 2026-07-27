package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ViewArchitectureTest {

    @Test
    fun `public composable screens use Screen suffix and feature package`() {
        screens.assertTrue(
            strict = true,
            additionalMessage = SCREEN_ORIGIN,
        ) { screen ->
            screen.name.endsWith("Screen") &&
                FEATURE_SCREEN_PACKAGE.matches(screen.packagee?.name.orEmpty()) &&
                screen.hasAnnotationWithName("Composable")
        }
    }

    @Test
    fun `screens are stateless boundaries without data or navigation dependencies`() {
        screenFiles.assertTrue(
            strict = true,
            additionalMessage = STATELESS_ORIGIN,
        ) { file ->
            file.importNames.none { dependency ->
                forbiddenScreenDependencies.any(dependency::startsWith)
            } &&
                "NavController" !in file.text &&
                "ViewModel" !in file.text
        }
    }

    @Test
    fun `destinations collect state with lifecycle`() {
        destinations.assertTrue(
            strict = true,
            additionalMessage = DESTINATION_ORIGIN,
        ) { destination ->
            destination.name.endsWith("Destination") &&
                destination.hasAnnotationWithName("Composable") &&
                "collectAsStateWithLifecycle" in destination.text
        }
    }

    @Test
    fun `navigation declarations use Route suffix and serializable contract`() {
        val routes = ArchitectureScope.productionFiles
            .filter { it.packageName == "com.cinemateca.navigation" }
            .flatMap { file ->
                file.classes(includeNested = false, includeLocal = false) +
                    file.objects(includeNested = false)
            }
            .filter { it.name.endsWith("Route") }

        routes.assertTrue(
            strict = true,
            additionalMessage = ROUTE_ORIGIN,
        ) { route ->
            route.name.endsWith("Route") &&
                route.hasAnnotationWithName("Serializable")
        }
    }

    @Test
    fun `screen specific components stay internal in components package`() {
        val components = ArchitectureScope.productionFiles
            .filter {
                it.packageName.matches(
                    Regex("""com\.cinemateca\.features\.[\w]+\.[\w]+\.components"""),
                )
            }
            .flatMap { it.functions(includeNested = false, includeLocal = false) }
            .filter { it.hasAnnotationWithName("Composable") }

        components.assertTrue(
            strict = true,
            additionalMessage = COMPONENT_ORIGIN,
        ) { component ->
            (component.hasInternalModifier || component.hasPrivateModifier) &&
                !component.name.endsWith("Component")
        }
    }

    private val screenFiles
        get() = ArchitectureScope.productionFiles.filter { file ->
            file.functions().any { it.name.endsWith("Screen") }
        }

    private val screens
        get() = screenFiles
            .flatMap { it.functions(includeNested = false, includeLocal = false) }
            .filter { it.name.endsWith("Screen") }

    private val destinations
        get() = ArchitectureScope.productionFiles
            .flatMap { it.functions(includeNested = false, includeLocal = false) }
            .filter { it.name.endsWith("Destination") }

    private companion object {
        val FEATURE_SCREEN_PACKAGE =
            Regex("""com\.cinemateca\.features\.[a-zA-Z][\w]*\.[a-zA-Z][\w]*""")

        val forbiddenScreenDependencies = listOf(
            "com.cinemateca.domain.",
            "com.cinemateca.local.",
            "com.cinemateca.networking.",
            "retrofit2.",
            "androidx.room.",
        )

        const val SCREEN_ORIGIN =
            "Expected public Compose screens in feature.<feature>.<screen> with Screen suffix. " +
                "Source: references/view.md, section Convenções obrigatórias, rules 1-2."
        const val STATELESS_ORIGIN =
            "Expected Screen to receive immutable state and callbacks without ViewModel, NavController, UseCase or Repository. " +
                "Source: references/view.md, sections Convenções obrigatórias and Comunicação com ViewModel."
        const val DESTINATION_ORIGIN =
            "Expected Destination to be the stateful boundary and collect StateFlow with lifecycle. " +
                "Source: references/view.md, sections Destination and Consumo de StateFlow."
        const val ROUTE_ORIGIN =
            "Expected typed serializable navigation declarations with Route suffix. " +
                "Source: references/view.md, sections Convenções obrigatórias and Compose Navigation."
        const val COMPONENT_ORIGIN =
            "Expected screen-specific components to be internal and semantically named. " +
                "Source: references/view.md, sections Convenções obrigatórias and Componentes de tela."
    }
}
