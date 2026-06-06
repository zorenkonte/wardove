package com.app.wardove.ui.util

import androidx.compose.ui.graphics.Color
import com.app.wardove.data.local.entity.ClothingStatus

object ClothingOptions {
    val categories: List<String> = listOf(
        "Top",
        "Bottom",
        "Shoes",
        "Outerwear",
        "Accessory"
    )

    data class NamedColor(val name: String, val hex: String)

    val colors: List<NamedColor> = listOf(
        NamedColor("Black", "#000000"),
        NamedColor("White", "#FFFFFF"),
        NamedColor("Gray", "#808080"),
        NamedColor("Red", "#E53935"),
        NamedColor("Orange", "#FB8C00"),
        NamedColor("Yellow", "#FDD835"),
        NamedColor("Green", "#43A047"),
        NamedColor("Blue", "#1E88E5"),
        NamedColor("Navy", "#1A237E"),
        NamedColor("Purple", "#8E24AA"),
        NamedColor("Pink", "#EC407A"),
        NamedColor("Brown", "#6D4C41")
    )

    fun colorNameFor(hex: String?): String =
        colors.firstOrNull { it.hex.equals(hex, ignoreCase = true) }?.name ?: (hex ?: "")
}

fun parseHexColor(hex: String): Color =
    runCatching { Color(android.graphics.Color.parseColor(hex)) }
        .getOrDefault(Color.Gray)

fun statusDisplayName(status: String): String = when (status) {
    ClothingStatus.CLEAN -> "Clean"
    ClothingStatus.WORN -> "Worn"
    ClothingStatus.IN_LAUNDRY -> "In Laundry"
    else -> status
}

fun statusColor(status: String): Color = when (status) {
    ClothingStatus.CLEAN -> Color(0xFF2E7D32)
    ClothingStatus.WORN -> Color(0xFFEF6C00)
    ClothingStatus.IN_LAUNDRY -> Color(0xFF1565C0)
    else -> Color.DarkGray
}
