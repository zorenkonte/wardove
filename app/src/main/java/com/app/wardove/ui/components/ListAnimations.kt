package com.app.wardove.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FiniteAnimationSpec
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.grid.LazyGridItemScope
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import kotlinx.coroutines.delay

private const val STAGGER_MS = 35L
private const val MAX_STAGGER_STEPS = 8

/**
 * Expressive list motion for wardrobe cells:
 *  - a staggered fade/scale-in the first time a cell appears (keyed by [key], so it
 *    plays once per item, not on every recomposition), and
 *  - `animateItem()` so cells slide to their new slot when a filter, sort or
 *    status change reorders the list, instead of snapping.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LazyGridItemScope.animatedWardrobeCell(index: Int, key: Any): Modifier =
    Modifier
        .animateItem(
            fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
            placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
            fadeOutSpec = MaterialTheme.motionScheme.fastEffectsSpec()
        )
        .staggeredEntrance(index, key, MaterialTheme.motionScheme.defaultSpatialSpec())

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LazyItemScope.animatedWardrobeRow(index: Int, key: Any): Modifier =
    Modifier
        .animateItem(
            fadeInSpec = MaterialTheme.motionScheme.defaultEffectsSpec(),
            placementSpec = MaterialTheme.motionScheme.defaultSpatialSpec(),
            fadeOutSpec = MaterialTheme.motionScheme.fastEffectsSpec()
        )
        .staggeredEntrance(index, key, MaterialTheme.motionScheme.defaultSpatialSpec())

@Composable
private fun Modifier.staggeredEntrance(
    index: Int,
    key: Any,
    spec: FiniteAnimationSpec<Float>
): Modifier {
    val progress = remember(key) { Animatable(0f) }
    LaunchedEffect(key) {
        if (progress.value < 1f) {
            delay(index.coerceAtMost(MAX_STAGGER_STEPS) * STAGGER_MS)
            progress.animateTo(1f, spec)
        }
    }
    return graphicsLayer {
        val p = progress.value.coerceIn(0f, 1f)
        alpha = p
        val scale = 0.92f + 0.08f * p
        scaleX = scale
        scaleY = scale
    }
}
