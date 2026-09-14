package com.app.wardove.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.compose.ui.unit.sp
import com.app.wardove.R

private val googleFontProvider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val DmSerifDisplay = FontFamily(
    Font(googleFont = GoogleFont("DM Serif Display"), fontProvider = googleFontProvider)
)

val DmSans = FontFamily(
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = googleFontProvider),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = googleFontProvider, weight = FontWeight.Medium),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = googleFontProvider, weight = FontWeight.SemiBold),
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = googleFontProvider, weight = FontWeight.Bold)
)

/**
 * Every role is set explicitly so no component silently falls back to Roboto.
 * Display/headline = DM Serif Display, everything else = DM Sans.
 */
val WardoveTypography = Typography(
    displayLarge = TextStyle(fontFamily = DmSerifDisplay, fontSize = 32.sp, lineHeight = 40.sp),
    displayMedium = TextStyle(fontFamily = DmSerifDisplay, fontSize = 28.sp, lineHeight = 36.sp),
    displaySmall = TextStyle(fontFamily = DmSerifDisplay, fontSize = 26.sp, lineHeight = 32.sp),
    headlineLarge = TextStyle(fontFamily = DmSerifDisplay, fontSize = 24.sp, lineHeight = 32.sp),
    headlineMedium = TextStyle(fontFamily = DmSerifDisplay, fontSize = 20.sp, lineHeight = 28.sp),
    headlineSmall = TextStyle(fontFamily = DmSerifDisplay, fontSize = 18.sp, lineHeight = 24.sp),
    titleLarge = TextStyle(fontFamily = DmSans, fontSize = 18.sp, fontWeight = FontWeight.Medium, lineHeight = 24.sp),
    titleMedium = TextStyle(fontFamily = DmSans, fontSize = 15.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp),
    titleSmall = TextStyle(fontFamily = DmSans, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp),
    bodyLarge = TextStyle(fontFamily = DmSans, fontSize = 15.sp, lineHeight = 22.sp),
    bodyMedium = TextStyle(fontFamily = DmSans, fontSize = 13.sp, lineHeight = 18.sp),
    bodySmall = TextStyle(fontFamily = DmSans, fontSize = 12.sp, lineHeight = 16.sp),
    labelLarge = TextStyle(fontFamily = DmSans, fontSize = 14.sp, fontWeight = FontWeight.Medium, lineHeight = 20.sp),
    labelMedium = TextStyle(fontFamily = DmSans, fontSize = 12.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp),
    labelSmall = TextStyle(fontFamily = DmSans, fontSize = 11.sp, fontWeight = FontWeight.Medium, lineHeight = 16.sp)
)
