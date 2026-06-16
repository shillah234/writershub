package com.writershub.app.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun WritershubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) {
        darkColorScheme(
            primary = Primary,
            secondary = Secondary,
            tertiary = PrimaryLight,
            background = Color(0xFF1A1A2E),
            surface = Color(0xFF2D2D44),
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = Color.White,
            onSurface = Color.White,
            primaryContainer = PrimaryLight.copy(alpha = 0.2f),
            secondaryContainer = Secondary.copy(alpha = 0.2f)
        )
    } else {
        lightColorScheme(
            primary = Primary,
            secondary = Secondary,
            tertiary = PrimaryLight,
            background = Background,
            surface = Surface,
            onPrimary = Color.White,
            onSecondary = Color.White,
            onBackground = TextPrimary,
            onSurface = TextPrimary,
            primaryContainer = PrimaryLight.copy(alpha = 0.1f),
            secondaryContainer = Secondary.copy(alpha = 0.1f)
        )
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography(),
        content = content
    )
}