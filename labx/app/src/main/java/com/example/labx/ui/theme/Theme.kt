package com.example.labx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
private val DarkColorScheme = darkColorScheme(
    primary = Verdigris,
    onPrimary = Snow,

    secondary = PearlAqua,
    onSecondary = Onyx,

    background = Onyx,
    onBackground = Snow,

    surface = Graphite,
    onSurface = Snow,

    surfaceVariant = Graphite.copy(alpha = 0.85f),
    onSurfaceVariant = PearlAqua
)
private val LightColorScheme = lightColorScheme(
    primary = Verdigris,
    onPrimary = Snow,

    secondary = PearlAqua,
    onSecondary = Onyx,

    background = Snow,
    onBackground = Onyx,

    surface = Color.White,
    onSurface = Onyx,

    surfaceVariant = PearlAqua.copy(alpha = 0.18f),
    onSurfaceVariant = Graphite
)

@Composable
fun LevelUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme)DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
