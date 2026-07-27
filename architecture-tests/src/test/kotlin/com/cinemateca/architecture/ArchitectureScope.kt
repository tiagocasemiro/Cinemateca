package com.cinemateca.architecture

import com.lemonappdev.konsist.api.declaration.KoFileDeclaration
import com.lemonappdev.konsist.api.Konsist

internal object ArchitectureScope {
    val productionFiles: List<KoFileDeclaration> by lazy {
        Konsist
            .scopeFromProject()
            .files
            .filter { it.sourceSetName == "main" && "/build/" !in it.path }
    }
}

internal val KoFileDeclaration.packageName: String
    get() = packagee?.name.orEmpty()

internal val KoFileDeclaration.importNames: List<String>
    get() = imports.map { it.name }
