package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

val LocalThemePreset = staticCompositionLocalOf { ThemePresets.CYBER_CYAN }
val LocalThemeAccent = staticCompositionLocalOf { Color(0xFF00E5FF) }

@Composable
fun TaskPulseTheme(
    themePreset: AppThemePreset = ThemePresets.CYBER_CYAN,
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> themePreset.darkColorScheme
        else -> themePreset.lightColorScheme
    }

    CompositionLocalProvider(
        LocalThemePreset provides themePreset,
        LocalThemeAccent provides themePreset.primaryAccent
    ) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = Typography,
            content = content
        )
    }
}

