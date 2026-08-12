package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val CyberColorScheme = darkColorScheme(
    primary = CyberPurple,
    onPrimary = Color.White,
    primaryContainer = CyberDarkSurfaceVariant,
    onPrimaryContainer = CyberPurpleLight,
    secondary = CyberCyan,
    onSecondary = Color.Black,
    secondaryContainer = CyberDarkSurfaceVariant,
    onSecondaryContainer = CyberCyan,
    tertiary = CyberPink,
    background = CyberDarkBackground,
    onBackground = CyberTextPrimary,
    surface = CyberDarkSurface,
    onSurface = CyberTextPrimary,
    surfaceVariant = CyberDarkSurfaceVariant,
    onSurfaceVariant = CyberTextSecondary
)

@Composable
fun Img2ImgTheme(
    darkTheme: Boolean = true, // Default to sleek dark cyber theme
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CyberColorScheme,
        typography = Typography,
        content = content
    )
}
