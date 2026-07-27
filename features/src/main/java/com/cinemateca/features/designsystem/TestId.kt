package com.cinemateca.features.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag

internal fun Modifier.testId(testId: String?): Modifier =
    if (testId.isNullOrBlank()) this else testTag(testId)

internal fun String?.childTestId(child: String): String? =
    this?.takeIf(String::isNotBlank)?.let { parent -> "$parent.$child" }
