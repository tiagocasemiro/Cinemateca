package com.cinemateca.architecture

import com.lemonappdev.konsist.api.verify.assertTrue
import org.junit.Test

class RepositoryArchitectureTest {

    @Test
    fun `repository contracts are interfaces in the domain feature package`() {
        repositoryFiles.assertTrue(
            strict = true,
            additionalMessage = REPOSITORY_ORIGIN,
        ) { file ->
            file.moduleName == "domain" &&
                DOMAIN_REPOSITORY_PACKAGE.matches(file.packageName) &&
                file.interfaces(includeNested = false).all { it.name.endsWith("Repository") }
        }
    }

    @Test
    fun `remote repository contracts expose suspend domain results`() {
        val remoteContracts = repositoryFiles
            .flatMap { it.interfaces(includeNested = true) }
            .filter { it.name == "Remote" }

        remoteContracts.assertTrue(
            strict = true,
            additionalMessage = REMOTE_ORIGIN,
        ) { remote ->
            remote.functions().isNotEmpty() &&
                remote.functions().all { function ->
                    function.hasSuspendModifier &&
                        "Result<" in function.text &&
                        forbiddenContractTypes.none(function.text::contains)
                }
        }
    }

    @Test
    fun `remote implementations use adapter package suffix and fetchData`() {
        val remoteImplementations = ArchitectureScope.productionFiles
            .flatMap { it.classes(includeNested = false, includeLocal = false) }
            .filter { it.name.endsWith("RemoteImpl") }

        remoteImplementations.assertTrue(
            strict = true,
            additionalMessage = IMPLEMENTATION_ORIGIN,
        ) { implementation ->
            implementation.moduleName == "networking" &&
                implementation.packagee?.name == "com.cinemateca.networking.adapter" &&
                "fetchData" in implementation.text
        }
    }

    private val repositoryFiles
        get() = ArchitectureScope.productionFiles.filter {
            DOMAIN_REPOSITORY_PACKAGE.matches(it.packageName)
        }

    private companion object {
        val DOMAIN_REPOSITORY_PACKAGE =
            Regex("""com\.cinemateca\.domain\.[a-zA-Z][\w]*\.repository""")

        val forbiddenContractTypes = listOf(
            "retrofit2.Response",
            "Response<",
            "Dto",
            "DTO",
            "Dao",
            "Entity",
        )

        const val REPOSITORY_ORIGIN =
            "Expected a Repository interface in domain.<feature>.repository with Repository suffix. " +
                "Source: references/repository.md, section Convenções obrigatórias, rules 1-3."
        const val REMOTE_ORIGIN =
            "Expected nested Remote operations to be suspend and return domain Result without infrastructure types. " +
                "Source: references/repository.md, section Convenções obrigatórias, rules 4-6."
        const val IMPLEMENTATION_ORIGIN =
            "Expected networking.adapter RemoteImpl implementations to wrap remote work in fetchData. " +
                "Source: references/repository.md, sections Convenções obrigatórias and Executar a chamada com fetchData."
    }
}
