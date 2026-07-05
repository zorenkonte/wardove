package com.app.wardove.ui.navigation

import androidx.compose.animation.AnimatedVisibilityScope
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionScope
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.dp

/**
 * Scopes required for cross-destination shared element transitions. Provided by
 * [com.app.wardove.ui.navigation.WardoveNavHost] (which wraps the NavHost in a
 * `SharedTransitionLayout`) and threaded via CompositionLocal so shared components
 * such as [com.app.wardove.ui.components.ClothingImage] and their many callers need
 * no extra parameters. Both are null outside the layout, making [clothingSharedImage]
 * a safe no-op there (e.g. in @Preview).
 */
@OptIn(ExperimentalSharedTransitionApi::class)
val LocalSharedTransitionScope = compositionLocalOf<SharedTransitionScope?> { null }

val LocalNavAnimatedVisibilityScope = compositionLocalOf<AnimatedVisibilityScope?> { null }

/**
 * Marks a clothing photo as a shared element keyed by [itemId], so it glides and
 * expands between the wardrobe grid cell and the item-detail header (pokedex-style).
 * [shape] is the corner rounding applied while the element is lifted into the
 * transition overlay. No-op when the transition scopes are absent.
 */
@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun Modifier.clothingSharedImage(
    itemId: Long,
    shape: Shape = RoundedCornerShape(16.dp)
): Modifier {
    val sharedScope = LocalSharedTransitionScope.current ?: return this
    val visibilityScope = LocalNavAnimatedVisibilityScope.current ?: return this
    return with(sharedScope) {
        this@clothingSharedImage.sharedElement(
            rememberSharedContentState(key = "clothing-image-$itemId"),
            animatedVisibilityScope = visibilityScope,
            boundsTransform = { _, _ ->
                spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessMediumLow
                )
            },
            clipInOverlayDuringTransition = OverlayClip(shape)
        )
    }
}
