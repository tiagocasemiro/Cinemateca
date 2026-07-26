package com.cinemateca.features.designsystem

import androidx.annotation.PluralsRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource

sealed interface UiText {
    data class Dynamic(
        val value: String,
    ) : UiText

    data class Resource(
        @StringRes val resourceId: Int,
        val arguments: List<Any> = emptyList(),
    ) : UiText

    data class Plural(
        @PluralsRes val resourceId: Int,
        val quantity: Int,
        val arguments: List<Any> = emptyList(),
    ) : UiText

    companion object {
        fun resource(
            @StringRes resourceId: Int,
            vararg arguments: Any,
        ): UiText = Resource(
            resourceId = resourceId,
            arguments = arguments.toList(),
        )

        fun plural(
            @PluralsRes resourceId: Int,
            quantity: Int,
            vararg arguments: Any,
        ): UiText = Plural(
            resourceId = resourceId,
            quantity = quantity,
            arguments = arguments.toList(),
        )
    }
}

@Composable
fun UiText.asString(): String = when (this) {
    is UiText.Dynamic -> value
    is UiText.Resource -> stringResource(
        resourceId,
        *arguments.resolve().toTypedArray(),
    )
    is UiText.Plural -> pluralStringResource(
        resourceId,
        quantity,
        *arguments.resolve().toTypedArray(),
    )
}

@Composable
private fun List<Any>.resolve(): List<Any> {
    val resolved = ArrayList<Any>(size)
    for (argument in this) {
        resolved += if (argument is UiText) {
            argument.asString()
        } else {
            argument
        }
    }
    return resolved
}
