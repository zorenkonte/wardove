package com.app.wardove.ui.components

import androidx.annotation.RawRes
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.airbnb.lottie.LottieProperty
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.airbnb.lottie.compose.rememberLottieDynamicProperties
import com.airbnb.lottie.compose.rememberLottieDynamicProperty

/**
 * Theme-aware Lottie player for the bundled `res/raw/lottie_*.json` illustrations.
 *
 * The animations are authored with two named color slots so they read correctly
 * in light *and* dark mode and under dynamic color:
 *  - shapes named `ink`   → recolored to [ink]   (default: onBackground)
 *  - shapes named `paper` → recolored to [paper] (default: background)
 * Fixed brand accents (teal / amber / purple) are left untouched.
 */
@Composable
fun WardoveLottie(
    @RawRes animation: Int,
    modifier: Modifier = Modifier,
    iterations: Int = LottieConstants.IterateForever,
    isPlaying: Boolean = true,
    ink: Color = MaterialTheme.colorScheme.onBackground,
    paper: Color = MaterialTheme.colorScheme.background
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.RawRes(animation))
    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = iterations,
        isPlaying = isPlaying
    )
    val dynamicProperties = rememberLottieDynamicProperties(
        rememberLottieDynamicProperty(LottieProperty.COLOR, ink.toArgb(), "**", "ink"),
        rememberLottieDynamicProperty(LottieProperty.STROKE_COLOR, ink.toArgb(), "**", "ink"),
        rememberLottieDynamicProperty(LottieProperty.COLOR, paper.toArgb(), "**", "paper")
    )
    LottieAnimation(
        composition = composition,
        progress = { progress },
        modifier = modifier,
        dynamicProperties = dynamicProperties
    )
}
