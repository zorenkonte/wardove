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
    Font(googleFont = GoogleFont("DM Sans"), fontProvider = googleFontProvider)
)

val WardoveTypography = Typography(
    displayLarge = TextStyle(fontFamily = DmSerifDisplay, fontSize = 32.sp),
    headlineLarge = TextStyle(fontFamily = DmSerifDisplay, fontSize = 24.sp),
    headlineMedium = TextStyle(fontFamily = DmSerifDisplay, fontSize = 20.sp),
    titleLarge = TextStyle(fontFamily = DmSans, fontSize = 18.sp, fontWeight = FontWeight.Medium),
    titleMedium = TextStyle(fontFamily = DmSans, fontSize = 15.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontFamily = DmSans, fontSize = 15.sp),
    bodyMedium = TextStyle(fontFamily = DmSans, fontSize = 13.sp),
    labelSmall = TextStyle(fontFamily = DmSans, fontSize = 11.sp)
)
