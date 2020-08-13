package com.toggl.common.feature.compose.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightThemeColors = lightColors(
    primary = Pink,
    primaryVariant = PinkDark,
    onPrimary = PurpleDeep,
    secondary = Yellow,
    secondaryVariant = Yellow,
    onSecondary = PurpleDeep,
    surface = WhiteAlpine,
    background = White,
    error = RedDark
)

private val DarkThemeColors = darkColors(
    primary = Pink,
    primaryVariant = PinkDark,
    onPrimary = White,
    secondary = Yellow,
    onSecondary = PurpleDeep,
    surface = PurpleDeep,
    background = PurpleDeep,
    error = RedLight
)

@Composable
fun TogglTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colors = if (darkTheme) DarkThemeColors else LightThemeColors,
        typography = ThemeTypography,
        shapes = Shapes,
        content = content
    )
}
