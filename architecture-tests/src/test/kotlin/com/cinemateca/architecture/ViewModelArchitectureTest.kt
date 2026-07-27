package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class ViewModelArchitectureTest {

    @Test
    fun `view models follow feature package naming and base type`() {
        viewModels.assertTrue(
            strict = true,
            additionalMessage = TYPE_ORIGIN,
        ) { viewModel ->
            FEATURE_SCREEN_PACKAGE.matches(viewModel.packagee?.name.orEmpty()) &&
                viewModel.name.endsWith("ViewModel") &&
                Regex(""":\s*ViewModel\s*\(""").containsMatchIn(viewModel.text)
        }
    }

    @Test
    fun `view models access business data only through use cases`() {
        viewModelFiles.assertTrue(
            strict = true,
            additionalMessage = DEPENDENCY_ORIGIN,
        ) { file ->
            file.importNames.none { dependency ->
                forbiddenDependencies.any(dependency::startsWith)
            } &&
                forbiddenTypes.none(file.text::contains)
        }
    }

    @Test
    fun `mutable flows in view models are private`() {
        viewModels
            .flatMap { it.properties(includeNested = false) }
            .filter {
                "MutableStateFlow" in it.text || "MutableSharedFlow" in it.text
            }
            .assertTrue(
                strict = true,
                additionalMessage = STATE_ORIGIN,
            ) { it.hasPrivateModifier }
    }

    @Test
    fun `view models use lifecycle managed coroutine scope`() {
        viewModels.assertTrue(
            strict = true,
            additionalMessage = COROUTINE_ORIGIN,
        ) { viewModel ->
            "GlobalScope" !in viewModel.text &&
                "CoroutineScope(" !in viewModel.text &&
                "runBlocking" !in viewModel.text &&
                "Dispatchers.IO" !in viewModel.text
        }
    }

    private val viewModelFiles
        get() = ArchitectureScope.productionFiles.filter {
            it.packageName.startsWith("com.cinemateca.features.") &&
                it.classes().any { declaration -> declaration.name.endsWith("ViewModel") }
        }

    private val viewModels
        get() = viewModelFiles
            .flatMap { it.classes(includeNested = false, includeLocal = false) }
            .filter { it.name.endsWith("ViewModel") }

    private companion object {
        val FEATURE_SCREEN_PACKAGE =
            Regex("""com\.cinemateca\.features\.[a-zA-Z][\w]*\.[a-zA-Z][\w]*""")

        val forbiddenDependencies = listOf(
            "com.cinemateca.local.",
            "com.cinemateca.networking.",
            "retrofit2.",
            "okhttp3.",
            "androidx.room.",
        )

        val forbiddenTypes = listOf("Repository", "Gateway", "Dao", "Database", "Cache")

        const val TYPE_ORIGIN =
            "Expected feature.<feature>.<screen> classes with ViewModel suffix inheriting lifecycle ViewModel. " +
                "Source: references/view-model.md, section Convenções obrigatórias, rules 1-3."
        const val DEPENDENCY_ORIGIN =
            "Expected ViewModels to access business data only through UseCases. " +
                "Source: references/view-model.md, section Convenções obrigatórias, rules 4-6."
        const val STATE_ORIGIN =
            "Expected MutableStateFlow and MutableSharedFlow to remain private. " +
                "Source: references/view-model.md, sections Convenções obrigatórias and Estado com StateFlow."
        const val COROUTINE_ORIGIN =
            "Expected ViewModels to use viewModelScope without unmanaged scopes or duplicated IO dispatchers. " +
                "Source: references/view-model.md, section Coroutines."
    }
}
