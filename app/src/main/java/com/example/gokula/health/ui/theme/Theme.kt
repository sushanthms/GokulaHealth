package com.example.gokula.health.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// 🌿 GREEN FARM THEME

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF2E7D32),       // dark green
    secondary = Color(0xFF66BB6A),     // light green
    tertiary = Color(0xFFA5D6A7),

    background = Color(0xFFF1F8E9),    // very light green
    surface = Color.White,

    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color(0xFF1B5E20),
    onSurface = Color.Black
)

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF66BB6A),
    secondary = Color(0xFFA5D6A7),
    background = Color(0xFF1B5E20),
    surface = Color(0xFF2E7D32)
)

@Composable
fun GokulaHealthTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {

    // ❌ REMOVE dynamic color → keep your custom theme
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}