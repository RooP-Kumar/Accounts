package com.zen.accounts.presentation.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = DarkPrimary,
    secondary = DarkSecondary,
    tertiary = Pink80,
    background = DarkBackground,
    surface = DarkSurface,
    onBackground = Color.White,
    onSurface = Color(0xFFFFFFFF),
    surfaceDim = Color(0xFFA2A2A2)
)

private val LightColorScheme = lightColorScheme(
    primary = primary_color,
    secondary = secondary_color,
    tertiary = Pink80,
    background = Color.White,
    surface = lightSurface,
    onBackground = Color.Black,
    onSurface = Color(0xFF000000),
    surfaceDim = Color(0xFF979797)
)

@Composable
fun AccountsThemes(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val color = if (darkTheme) DarkColorScheme
    else LightColorScheme

    MaterialTheme(
        colorScheme = color,
        typography = Typography,
        content = content
    )
}