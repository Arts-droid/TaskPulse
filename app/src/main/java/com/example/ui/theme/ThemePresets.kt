package com.example.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

data class AppThemePreset(
    val id: String,
    val name: String,
    val category: String,
    val tag: String,
    val description: String,
    val primaryAccent: Color,
    val secondaryAccent: Color,
    val previewSwatches: List<Color>,
    val darkColorScheme: ColorScheme,
    val lightColorScheme: ColorScheme
)

object ThemePresets {

    // 1. Cyber Cyan (Default Iconic)
    val CYBER_CYAN = AppThemePreset(
        id = "CYBER_CYAN",
        name = "Cyber Cyan",
        category = "Cyber & Neon",
        tag = "ICONIC",
        description = "High-voltage neon cyan with emerald accents and cosmic dark surface.",
        primaryAccent = Color(0xFF00E5FF),
        secondaryAccent = Color(0xFF00E676),
        previewSwatches = listOf(
            Color(0xFF00E5FF),
            Color(0xFF00E676),
            Color(0xFFFFB300),
            Color(0xFF101726)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00E5FF),
            onPrimary = Color(0xFF00363D),
            primaryContainer = Color(0xFF004D56),
            onPrimaryContainer = Color(0xFF80F2FF),
            secondary = Color(0xFF00E676),
            onSecondary = Color(0xFF00381B),
            secondaryContainer = Color(0xFF00897B),
            onSecondaryContainer = Color(0xFF98FFB3),
            tertiary = Color(0xFFFFB300),
            onTertiary = Color(0xFF3E2800),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF090D16),
            onBackground = Color(0xFFF1F5F9),
            surface = Color(0xFF101726),
            onSurface = Color(0xFFF1F5F9),
            surfaceVariant = Color(0xFF192238),
            onSurfaceVariant = Color(0xFF94A3B8),
            outline = Color(0xFF263554)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF00838F),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE0F7FA),
            onPrimaryContainer = Color(0xFF002025),
            secondary = Color(0xFF00897B),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F2F1),
            onSecondaryContainer = Color(0xFF00210E),
            tertiary = Color(0xFFFFB300),
            onTertiary = Color(0xFF3E2800),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFF4F8FA),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE6EEF2),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFCBD5E1)
        )
    )

    // 2. Neon Matrix (Terminal Green)
    val NEON_MATRIX = AppThemePreset(
        id = "NEON_MATRIX",
        name = "Neon Matrix",
        category = "Cyber & Neon",
        tag = "TERMINAL",
        description = "Intense hacker green phosphor display with deep obsidian contrast.",
        primaryAccent = Color(0xFF00FF66),
        secondaryAccent = Color(0xFF76FF03),
        previewSwatches = listOf(
            Color(0xFF00FF66),
            Color(0xFF76FF03),
            Color(0xFF00E5FF),
            Color(0xFF0B190E)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF00FF66),
            onPrimary = Color(0xFF003912),
            primaryContainer = Color(0xFF00521C),
            onPrimaryContainer = Color(0xFF6DFF94),
            secondary = Color(0xFF76FF03),
            onSecondary = Color(0xFF183800),
            secondaryContainer = Color(0xFF295A00),
            onSecondaryContainer = Color(0xFFA8FF52),
            tertiary = Color(0xFF00E5FF),
            onTertiary = Color(0xFF00363D),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF050E07),
            onBackground = Color(0xFFEEF9EF),
            surface = Color(0xFF0B190E),
            onSurface = Color(0xFFEEF9EF),
            surfaceVariant = Color(0xFF132817),
            onSurfaceVariant = Color(0xFF86A38B),
            outline = Color(0xFF1E3F25)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF008A37),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFDCFCE7),
            onPrimaryContainer = Color(0xFF002209),
            secondary = Color(0xFF4D9900),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFECFCCB),
            onSecondaryContainer = Color(0xFF142900),
            tertiary = Color(0xFF00838F),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFF2FBF4),
            onBackground = Color(0xFF0E1A10),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0E1A10),
            surfaceVariant = Color(0xFFE1EFE4),
            onSurfaceVariant = Color(0xFF415646),
            outline = Color(0xFFC2D9C7)
        )
    )

    // 3. Synthwave Sunset (80s Retro Outrun)
    val SYNTHWAVE_SUNSET = AppThemePreset(
        id = "SYNTHWAVE_SUNSET",
        name = "Synthwave 80s",
        category = "Retro & Cosmic",
        tag = "RETRO",
        description = "Hot neon fuchsia, electric magenta, and twilight violet grid vibes.",
        primaryAccent = Color(0xFFFF2A85),
        secondaryAccent = Color(0xFFB388FF),
        previewSwatches = listOf(
            Color(0xFFFF2A85),
            Color(0xFFB388FF),
            Color(0xFFFF9E00),
            Color(0xFF1E1030)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF2A85),
            onPrimary = Color(0xFF3E001A),
            primaryContainer = Color(0xFF6B0031),
            onPrimaryContainer = Color(0xFFFFB0D0),
            secondary = Color(0xFFB388FF),
            onSecondary = Color(0xFF280061),
            secondaryContainer = Color(0xFF4A148C),
            onSecondaryContainer = Color(0xFFE1BEE7),
            tertiary = Color(0xFFFF9E00),
            onTertiary = Color(0xFF422100),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF10071C),
            onBackground = Color(0xFFFCEEFA),
            surface = Color(0xFF1C0F2F),
            onSurface = Color(0xFFFCEEFA),
            surfaceVariant = Color(0xFF2A1944),
            onSurfaceVariant = Color(0xFFB7A5C9),
            outline = Color(0xFF452D6B)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFC2185B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFCE4EC),
            onPrimaryContainer = Color(0xFF370016),
            secondary = Color(0xFF6A1B9A),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF3E5F5),
            onSecondaryContainer = Color(0xFF240038),
            tertiary = Color(0xFFF57C00),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFFAF3F7),
            onBackground = Color(0xFF200E1B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF200E1B),
            surfaceVariant = Color(0xFFEFE2EC),
            onSurfaceVariant = Color(0xFF5B4555),
            outline = Color(0xFFDAC7D5)
        )
    )

    // 4. Solar Flare (Amber Inferno)
    val SOLAR_FLARE = AppThemePreset(
        id = "SOLAR_FLARE",
        name = "Solar Flare",
        category = "Cyber & Neon",
        tag = "HOT",
        description = "Blazing electric orange and gold embers with intense performance styling.",
        primaryAccent = Color(0xFFFF6D00),
        secondaryAccent = Color(0xFFFFD600),
        previewSwatches = listOf(
            Color(0xFFFF6D00),
            Color(0xFFFFD600),
            Color(0xFFFF3D00),
            Color(0xFF24150A)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF6D00),
            onPrimary = Color(0xFF411700),
            primaryContainer = Color(0xFF6E2800),
            onPrimaryContainer = Color(0xFFFFD0B0),
            secondary = Color(0xFFFFD600),
            onSecondary = Color(0xFF3F3400),
            secondaryContainer = Color(0xFF695800),
            onSecondaryContainer = Color(0xFFFFF199),
            tertiary = Color(0xFFFF3D00),
            onTertiary = Color(0xFF450900),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF140B04),
            onBackground = Color(0xFFFDF4EE),
            surface = Color(0xFF22140A),
            onSurface = Color(0xFFFDF4EE),
            surfaceVariant = Color(0xFF332012),
            onSurfaceVariant = Color(0xFFBFA695),
            outline = Color(0xFF573822)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFD84315),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFBE9E7),
            onPrimaryContainer = Color(0xFF3B0E03),
            secondary = Color(0xFFF57F17),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFFDE7),
            onSecondaryContainer = Color(0xFF382300),
            tertiary = Color(0xFFE65100),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFFFF9F5),
            onBackground = Color(0xFF24140B),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF24140B),
            surfaceVariant = Color(0xFFF5E7DF),
            onSurfaceVariant = Color(0xFF634D41),
            outline = Color(0xFFE2CFC5)
        )
    )

    // 5. Crimson Core (Cyberpunk Red)
    val CRIMSON_CORE = AppThemePreset(
        id = "CRIMSON_CORE",
        name = "Crimson Core",
        category = "Cyber & Neon",
        tag = "STEALTH",
        description = "Aggressive cyber red and bright coral for extreme high-load alerts.",
        primaryAccent = Color(0xFFFF1744),
        secondaryAccent = Color(0xFFFF5252),
        previewSwatches = listOf(
            Color(0xFFFF1744),
            Color(0xFFFF5252),
            Color(0xFFFF9100),
            Color(0xFF240D10)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFFF1744),
            onPrimary = Color(0xFF44000D),
            primaryContainer = Color(0xFF75001C),
            onPrimaryContainer = Color(0xFFFFB3BC),
            secondary = Color(0xFFFF5252),
            onSecondary = Color(0xFF450005),
            secondaryContainer = Color(0xFF700010),
            onSecondaryContainer = Color(0xFFFFB4AB),
            tertiary = Color(0xFFFF9100),
            onTertiary = Color(0xFF412000),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF140809),
            onBackground = Color(0xFFFDEEEE),
            surface = Color(0xFF220D10),
            onSurface = Color(0xFFFDEEEE),
            surfaceVariant = Color(0xFF331519),
            onSurfaceVariant = Color(0xFFBFA0A4),
            outline = Color(0xFF55252C)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFFC62828),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFFFEBEE),
            onPrimaryContainer = Color(0xFF400508),
            secondary = Color(0xFFD32F2F),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFFFEBEE),
            onSecondaryContainer = Color(0xFF3F0407),
            tertiary = Color(0xFFEF6C00),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFFFF6F6),
            onBackground = Color(0xFF220C0E),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF220C0E),
            surfaceVariant = Color(0xFFF5E2E4),
            onSurfaceVariant = Color(0xFF614749),
            outline = Color(0xFFDFC6C8)
        )
    )

    // 6. Nebula Violet (Cosmic Void)
    val NEBULA_VIOLET = AppThemePreset(
        id = "NEBULA_VIOLET",
        name = "Nebula Violet",
        category = "Retro & Cosmic",
        tag = "COSMIC",
        description = "Deep galactic indigo, starlight violet, and celestial ice accents.",
        primaryAccent = Color(0xFFB388FF),
        secondaryAccent = Color(0xFF00E5FF),
        previewSwatches = listOf(
            Color(0xFFB388FF),
            Color(0xFF7C4DFF),
            Color(0xFF00E5FF),
            Color(0xFF181331)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFB388FF),
            onPrimary = Color(0xFF26005D),
            primaryContainer = Color(0xFF4A148C),
            onPrimaryContainer = Color(0xFFE6D6FF),
            secondary = Color(0xFF00E5FF),
            onSecondary = Color(0xFF00363D),
            secondaryContainer = Color(0xFF006064),
            onSecondaryContainer = Color(0xFF80F2FF),
            tertiary = Color(0xFFE040FB),
            onTertiary = Color(0xFF3E004A),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF0C091A),
            onBackground = Color(0xFFF4F0FE),
            surface = Color(0xFF16122C),
            onSurface = Color(0xFFF4F0FE),
            surfaceVariant = Color(0xFF221C42),
            onSurfaceVariant = Color(0xFFA59EC2),
            outline = Color(0xFF382E66)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF651FFF),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFEDE7F6),
            onPrimaryContainer = Color(0xFF1A0057),
            secondary = Color(0xFF0097A7),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F7FA),
            onSecondaryContainer = Color(0xFF002A30),
            tertiary = Color(0xFFAA00FF),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFF7F5FC),
            onBackground = Color(0xFF140F26),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF140F26),
            surfaceVariant = Color(0xFFEBE6F5),
            onSurfaceVariant = Color(0xFF50466A),
            outline = Color(0xFFD1C8E5)
        )
    )

    // 7. Oceanic Ice (Cobalt Blue)
    val OCEANIC_ICE = AppThemePreset(
        id = "OCEANIC_ICE",
        name = "Oceanic Ice",
        category = "Cyber & Neon",
        tag = "POPULAR",
        description = "Royal electric cobalt, arctic glacier azure, and clean high-tech focus.",
        primaryAccent = Color(0xFF2979FF),
        secondaryAccent = Color(0xFF00E5FF),
        previewSwatches = listOf(
            Color(0xFF2979FF),
            Color(0xFF00E5FF),
            Color(0xFF00E676),
            Color(0xFF0E1D36)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFF2979FF),
            onPrimary = Color(0xFF002266),
            primaryContainer = Color(0xFF003EAA),
            onPrimaryContainer = Color(0xFFB8D3FF),
            secondary = Color(0xFF00E5FF),
            onSecondary = Color(0xFF00363D),
            secondaryContainer = Color(0xFF006064),
            onSecondaryContainer = Color(0xFF80F2FF),
            tertiary = Color(0xFF00E676),
            onTertiary = Color(0xFF00381B),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF060F1E),
            onBackground = Color(0xFFF0F5FD),
            surface = Color(0xFF0E1D36),
            onSurface = Color(0xFFF0F5FD),
            surfaceVariant = Color(0xFF152A4D),
            onSurfaceVariant = Color(0xFF91A7CD),
            outline = Color(0xFF23447A)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1565C0),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE3F2FD),
            onPrimaryContainer = Color(0xFF03224C),
            secondary = Color(0xFF00838F),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFE0F7FA),
            onSecondaryContainer = Color(0xFF002529),
            tertiary = Color(0xFF00897B),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFF3F7FC),
            onBackground = Color(0xFF0A182F),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0A182F),
            surfaceVariant = Color(0xFFE3ECF8),
            onSurfaceVariant = Color(0xFF455776),
            outline = Color(0xFFC7D7EC)
        )
    )

    // 8. Titanium Minimal (Monochrome Precision)
    val TITANIUM_MINIMAL = AppThemePreset(
        id = "TITANIUM_MINIMAL",
        name = "Titanium Minimal",
        category = "Minimalist",
        tag = "CLEAN",
        description = "Sleek industrial titanium, graphite carbon, and hyper-clean monochrome lines.",
        primaryAccent = Color(0xFFE2E8F0),
        secondaryAccent = Color(0xFF94A3B8),
        previewSwatches = listOf(
            Color(0xFFE2E8F0),
            Color(0xFF94A3B8),
            Color(0xFF00E5FF),
            Color(0xFF161E2E)
        ),
        darkColorScheme = darkColorScheme(
            primary = Color(0xFFE2E8F0),
            onPrimary = Color(0xFF0F172A),
            primaryContainer = Color(0xFF334155),
            onPrimaryContainer = Color(0xFFF8FAFC),
            secondary = Color(0xFF94A3B8),
            onSecondary = Color(0xFF0F172A),
            secondaryContainer = Color(0xFF475569),
            onSecondaryContainer = Color(0xFFF1F5F9),
            tertiary = Color(0xFF00E5FF),
            onTertiary = Color(0xFF00363D),
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFF0B0F17),
            onBackground = Color(0xFFF8FAFC),
            surface = Color(0xFF161E2E),
            onSurface = Color(0xFFF8FAFC),
            surfaceVariant = Color(0xFF202B3E),
            onSurfaceVariant = Color(0xFFA0AEC0),
            outline = Color(0xFF334155)
        ),
        lightColorScheme = lightColorScheme(
            primary = Color(0xFF1E293B),
            onPrimary = Color.White,
            primaryContainer = Color(0xFFE2E8F0),
            onPrimaryContainer = Color(0xFF0F172A),
            secondary = Color(0xFF475569),
            onSecondary = Color.White,
            secondaryContainer = Color(0xFFF1F5F9),
            onSecondaryContainer = Color(0xFF0F172A),
            tertiary = Color(0xFF00838F),
            onTertiary = Color.White,
            error = Color(0xFFFF5252),
            onError = Color.White,
            background = Color(0xFFF8FAFC),
            onBackground = Color(0xFF0F172A),
            surface = Color(0xFFFFFFFF),
            onSurface = Color(0xFF0F172A),
            surfaceVariant = Color(0xFFE2E8F0),
            onSurfaceVariant = Color(0xFF475569),
            outline = Color(0xFFCBD5E1)
        )
    )

    val allPresets: List<AppThemePreset> = listOf(
        CYBER_CYAN,
        NEON_MATRIX,
        SYNTHWAVE_SUNSET,
        SOLAR_FLARE,
        CRIMSON_CORE,
        NEBULA_VIOLET,
        OCEANIC_ICE,
        TITANIUM_MINIMAL
    )

    val categories: List<String> = listOf("All", "Cyber & Neon", "Retro & Cosmic", "Minimalist")

    fun getById(id: String?): AppThemePreset {
        return allPresets.firstOrNull { it.id.equals(id, ignoreCase = true) } ?: CYBER_CYAN
    }
}
