package com.cinemateca.features.designsystem

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object CinematecaColors {
    val Background = Color(0xFF0A0A0F)
    val Surface = Color(0xFF13131A)
    val SurfaceElevated = Color(0xFF1C1C26)
    val ButtonSurface = Color(0xFF222230)
    val Primary = Color(0xFF4D8EFF)
    val Favorite = Color(0xFFFF4D6A)
    val OnBackground = Color.White
    val SecondaryText = Color.White.copy(alpha = 0.5f)
    val TertiaryText = Color.White.copy(alpha = 0.3f)
    val ButtonText = Color(0xFFC0C0D8)
    val Outline = Color.White.copy(alpha = 0.07f)
    val ButtonOutline = Color.White.copy(alpha = 0.14f)
}

private val CinematecaDarkColorScheme = darkColorScheme(
    primary = CinematecaColors.Primary,
    background = CinematecaColors.Background,
    surface = CinematecaColors.Surface,
    onBackground = CinematecaColors.OnBackground,
    onSurface = CinematecaColors.OnBackground,
)

@Composable
fun CinematecaTheme(
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CinematecaDarkColorScheme,
        content = content,
    )
}
