package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme = darkColorScheme(
    primary = ElegantPrimary,
    onPrimary = OnElegantPrimary,
    primaryContainer = ElegantPrimaryContainer,
    onPrimaryContainer = OnElegantPrimaryContainer,
    secondary = AccentPurple,
    onSecondary = OnElegantPrimary,
    tertiary = CipherGreen,
    background = ElegantBackground,
    onBackground = ElegantTextPrimary,
    surface = ElegantSurface,
    onSurface = ElegantTextPrimary,
    surfaceVariant = ElegantSurfaceVariant,
    onSurfaceVariant = ElegantTextSecondary,
    outline = ElegantBorder,
    outlineVariant = ElegantBorderSubtle,
    error = DangerRed
)

private val LightColorScheme = DarkColorScheme // Default to Elegant Dark for unified high-end aesthetic

@Composable
fun WhisperTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
