package com.app.wardove.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialExpressiveTheme
import androidx.compose.material3.MotionScheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.app.wardove.data.settings.ThemeMode

/**
 * Brand palette mapped onto the full Material 3 color roles so Expressive
 * components (tonal buttons, button groups, loading indicators, containers)
 * pick up the warm off-white / near-black identity instead of the default
 * purple baseline. Secondary = clean-teal, tertiary = laundry-purple.
 */
val WardoveLightColors = lightColorScheme(
    primary = Color(0xFF1A1A1A),
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFE4E1DC),
    onPrimaryContainer = Color(0xFF1A1A1A),
    inversePrimary = Color(0xFFEDEDED),
    secondary = Color(0xFF2E8B6F),
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFFD6F2E8),
    onSecondaryContainer = Color(0xFF0F3D30),
    tertiary = Color(0xFF5B52C4),
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFE6E3FA),
    onTertiaryContainer = Color(0xFF2B2470),
    background = Color(0xFFF7F5F2),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEBE8E3),
    onSurfaceVariant = Color(0xFF555555),
    surfaceDim = Color(0xFFE8E5E1),
    surfaceBright = Color(0xFFFFFFFF),
    surfaceContainerLowest = Color(0xFFFFFFFF),
    surfaceContainerLow = Color(0xFFFAF8F6),
    surfaceContainer = Color(0xFFF1EEEA),
    surfaceContainerHigh = Color(0xFFEBE8E3),
    surfaceContainerHighest = Color(0xFFE4E1DC),
    inverseSurface = Color(0xFF1A1A1A),
    inverseOnSurface = Color(0xFFF7F5F2),
    outline = Color(0xFFE0DDD8),
    outlineVariant = Color(0xFFEBE8E3),
    scrim = Color(0xFF000000)
)

val WardoveDarkColors = darkColorScheme(
    primary = Color(0xFFEDEDED),
    onPrimary = Color(0xFF1A1A1A),
    primaryContainer = Color(0xFF3A3A3A),
    onPrimaryContainer = Color(0xFFEDEDED),
    inversePrimary = Color(0xFF1A1A1A),
    secondary = Color(0xFF7ED9B9),
    onSecondary = Color(0xFF00382A),
    secondaryContainer = Color(0xFF1F5A47),
    onSecondaryContainer = Color(0xFFD6F2E8),
    tertiary = Color(0xFFB3ACF2),
    onTertiary = Color(0xFF221B66),
    tertiaryContainer = Color(0xFF3F378F),
    onTertiaryContainer = Color(0xFFE6E3FA),
    background = Color(0xFF121212),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF1E1E1E),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF2A2A2A),
    onSurfaceVariant = Color(0xFFBBBBBB),
    surfaceDim = Color(0xFF121212),
    surfaceBright = Color(0xFF3A3A3A),
    surfaceContainerLowest = Color(0xFF0D0D0D),
    surfaceContainerLow = Color(0xFF1A1A1A),
    surfaceContainer = Color(0xFF1E1E1E),
    surfaceContainerHigh = Color(0xFF282828),
    surfaceContainerHighest = Color(0xFF333333),
    inverseSurface = Color(0xFFEDEDED),
    inverseOnSurface = Color(0xFF1A1A1A),
    outline = Color(0xFF3A3A3A),
    outlineVariant = Color(0xFF2A2A2A),
    scrim = Color(0xFF000000)
)

/** Rounder, softer corner family per M3 Expressive shape guidance. */
val WardoveShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(16.dp),
    large = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)

/** Resolves the user's [ThemeMode] against the system setting. */
@Composable
fun isDarkTheme(themeMode: ThemeMode): Boolean = when (themeMode) {
    ThemeMode.SYSTEM -> isSystemInDarkTheme()
    ThemeMode.LIGHT -> false
    ThemeMode.DARK -> true
}

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WardoveTheme(
    themeMode: ThemeMode = ThemeMode.SYSTEM,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val dark = isDarkTheme(themeMode)
    val context = LocalContext.current
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ->
            if (dark) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        dark -> WardoveDarkColors
        else -> WardoveLightColors
    }
    // MaterialExpressiveTheme swaps in the M3 Expressive defaults: spring-based
    // motion for every component, plus expressive component styling.
    MaterialExpressiveTheme(
        colorScheme = colorScheme,
        motionScheme = MotionScheme.expressive(),
        shapes = WardoveShapes,
        typography = WardoveTypography,
        content = content
    )
}
