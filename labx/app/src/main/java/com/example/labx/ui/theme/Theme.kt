package com.example.labx.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MoradoClaro,
    onPrimary = Color.White,

    secondary = MoradoFuerte,
    onSecondary = Color.White,

    tertiary = MoradoProfundo,
    onTertiary = Color.White,

    background = AzulNegro,
    onBackground = Color.White,

    surface = MoradoProfundo,
    onSurface = Color.White,

    surfaceVariant = MoradoFuerte.copy(alpha = 0.6f),
    onSurfaceVariant = BlancoLila
)
private val LightColorScheme = lightColorScheme(
    primary = MoradoFuerte,
    onPrimary = Color.White,

    secondary = MoradoClaro,
    onSecondary = Color.White,

    tertiary = MoradoProfundo,
    onTertiary = Color.White,

    background = BlancoLila,
    onBackground = AzulNegro,

    surface = Color.White,
    onSurface = AzulNegro,

    surfaceVariant = MoradoClaro.copy(alpha = 0.18f),
    onSurfaceVariant = MoradoProfundo
)

@Composable
fun LevelUpTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}
