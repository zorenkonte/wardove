package com.app.wardove.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val WardoveLightColors = lightColorScheme(
    primary = Color(0xFF1A1A1A),
    onPrimary = Color(0xFFFFFFFF),
    background = Color(0xFFF7F5F2),
    onBackground = Color(0xFF1A1A1A),
    surface = Color(0xFFFFFFFF),
    onSurface = Color(0xFF1A1A1A),
    surfaceVariant = Color(0xFFEBE8E3),
    onSurfaceVariant = Color(0xFF555555),
    outline = Color(0xFFE0DDD8)
)

@Composable
fun WardoveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = WardoveLightColors,
        typography = WardoveTypography,
        content = content
    )
}
