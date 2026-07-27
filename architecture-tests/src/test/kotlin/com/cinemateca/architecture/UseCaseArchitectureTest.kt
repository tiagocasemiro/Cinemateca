package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class UseCaseArchitectureTest {

    @Test
    fun `use cases follow domain package and naming conventions`() {
        useCases.assertTrue(
            strict = true,
            additionalMessage = NAMING_ORIGIN,
        ) { useCase ->
            useCase.moduleName == "domain" &&
                USE_CASE_PACKAGE.matches(useCase.packagee?.name.orEmpty()) &&
                useCase.name.endsWith("UseCase")
        }
    }

    @Test
    fun `use cases do not expose infrastructure Android or UI`() {
        useCaseFiles.assertTrue(
            strict = true,
            additionalMessage = DEPENDENCY_ORIGIN,
        ) { file ->
            file.importNames.none { dependency ->
                forbiddenDependencies.any(dependency::startsWith)
            } &&
                forbiddenText.none(file.text::contains)
        }
    }

    @Test
    fun `use cases do not create unmanaged coroutine scopes`() {
        useCases.assertTrue(
            strict = true,
            additionalMessage = COROUTINE_ORIGIN,
        ) { useCase ->
            "GlobalScope" !in useCase.text &&
                "CoroutineScope(" !in useCase.text &&
                "Dispatchers.Main" !in useCase.text
        }
    }

    private val useCaseFiles
        get() = ArchitectureScope.productionFiles.filter {
            USE_CASE_PACKAGE.matches(it.packageName)
        }

    private val useCases
        get() = useCaseFiles.flatMap {
            it.classes(includeNested = false, includeLocal = false)
        }

    private companion object {
        val USE_CASE_PACKAGE =
            Regex("""com\.cinemateca\.domain\.[a-zA-Z][\w]*\.usecase""")

        val forbiddenDependencies = listOf(
            "android.",
            "androidx.",
            "retrofit2.",
            "okhttp3.",
            "androidx.room.",
            "com.cinemateca.features.",
            "com.cinemateca.local.",
            "com.cinemateca.networking.",
        )

        val forbiddenText = listOf("Response<", "Dto", "DTO", "Dao", "Entity")

        const val NAMING_ORIGIN =
            "Expected domain.<feature>.usecase classes with UseCase suffix. " +
                "Source: references/use-case.md, section Convenções obrigatórias, rules 1-3."
        const val DEPENDENCY_ORIGIN =
            "Expected UseCases to depend on Repository contracts and expose only domain types. " +
                "Source: references/use-case.md, section Convenções obrigatórias, rules 4-8."
        const val COROUTINE_ORIGIN =
            "Expected lifecycle-safe structured concurrency without Main, GlobalScope or unmanaged CoroutineScope. " +
                "Source: references/use-case.md, sections Convenções obrigatórias and Coroutines."
    }
}
