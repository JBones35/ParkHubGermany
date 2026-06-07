package com.parkhub.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColorScheme = lightColorScheme(
    primary = ParkHubGreen,
    onPrimary = White,
    primaryContainer = ParkHubGreenContainer,
    onPrimaryContainer = ParkHubGreenDark,
    secondary = ParkHubGreenLight,
    onSecondary = White,
    background = White,
    onBackground = Black,
    surface = GrayLight,
    onSurface = Black,
    surfaceVariant = GrayLight,
    outline = GrayBorder,
    error = RedError,
    onError = White,
    secondaryContainer = SecondaryContainerLight
)

private val DarkColorScheme = darkColorScheme(
    primary = ParkHubGreenLight,
    onPrimary = Black,
    primaryContainer = ParkHubGreenContainerDark,
    onPrimaryContainer = ParkHubGreenLight,
    secondary = ParkHubGreenLight,
    onSecondary = Black,
    background = DarkBackground,
    onBackground = White,
    surface = DarkSurface,
    onSurface = White,
    surfaceVariant = DarkCard,
    outline = Gray,
    error = RedError,
    onError = White,
    secondaryContainer = SecondaryContainerDark
)

@Composable
fun ParkHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}