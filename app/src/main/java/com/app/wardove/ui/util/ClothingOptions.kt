package com.app.wardove.ui.util

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import com.app.wardove.R
import com.app.wardove.data.local.entity.ClothingStatus

object ClothingOptions {
    val categories: List<String> = listOf(
        "Top",
        "Bottom",
        "Shoes",
        "Outerwear",
        "Accessory"
    )

    /** Returns the string resource ID for [category] (the DB key), or null if unknown. */
    @StringRes
    fun categoryResId(category: String): Int? = when (category) {
        "Top" -> R.string.category_top
        "Bottom" -> R.string.category_bottom
        "Shoes" -> R.string.category_shoes
        "Outerwear" -> R.string.category_outerwear
        "Accessory" -> R.string.category_accessory
        else -> null
    }

    data class NamedColor(val name: String, val hex: String, @StringRes val nameResId: Int)

    val colors: List<NamedColor> = listOf(
        NamedColor("Black",  "#000000", R.string.color_black),
        NamedColor("White",  "#FFFFFF", R.string.color_white),
        NamedColor("Gray",   "#808080", R.string.color_gray),
        NamedColor("Red",    "#E53935", R.string.color_red),
        NamedColor("Orange", "#FB8C00", R.string.color_orange),
        NamedColor("Yellow", "#FDD835", R.string.color_yellow),
        NamedColor("Green",  "#43A047", R.string.color_green),
        NamedColor("Blue",   "#1E88E5", R.string.color_blue),
        NamedColor("Navy",   "#1A237E", R.string.color_navy),
        NamedColor("Purple", "#8E24AA", R.string.color_purple),
        NamedColor("Pink",   "#EC407A", R.string.color_pink),
        NamedColor("Brown",  "#6D4C41", R.string.color_brown)
    )

    /** Returns the string resource ID for the color with [hex], or null if unknown. */
    @StringRes
    fun colorNameResId(hex: String?): Int? =
        colors.firstOrNull { it.hex.equals(hex, ignoreCase = true) }?.nameResId

    /** Fallback: returns the English display name for [hex] without using resources. */
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
