package com.app.wardove.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.awaitVerticalTouchSlopOrCancellation
import androidx.compose.foundation.gestures.verticalDrag
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.positionChange
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch

/**
 * Photo-viewer style dismiss: dragging the element downward pulls it along (with a
 * slight shrink), and releasing past [dismissThreshold] fires [onDismiss]. Releasing
 * earlier springs it back.
 *
 * Plays nicely with a parent `verticalScroll`: the gesture is only claimed when the
 * first movement past touch slop is *downward* and [scrollState] is at the top;
 * otherwise the events are left alone and the page scrolls as usual.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Modifier.swipeDownToDismiss(
    scrollState: ScrollState,
    onDismiss: () -> Unit,
    enabled: Boolean = true,
    dismissThreshold: androidx.compose.ui.unit.Dp = 120.dp
): Modifier {
    if (!enabled) return this
    val offsetY = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()
    val haptic = LocalHapticFeedback.current
    val thresholdPx = with(LocalDensity.current) { dismissThreshold.toPx() }
    val currentOnDismiss by rememberUpdatedState(onDismiss)
    val settleSpec = MaterialTheme.motionScheme.defaultSpatialSpec<Float>()

    return this
        .graphicsLayer {
            val progress = (offsetY.value / (thresholdPx * 2f)).coerceIn(0f, 1f)
            translationY = offsetY.value
            val scale = 1f - 0.12f * progress
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0.5f, 0f)
        }
        .pointerInput(scrollState, thresholdPx) {
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                // Decide who owns the gesture once the finger has moved past slop.
                var claimed = false
                val slopChange = awaitVerticalTouchSlopOrCancellation(down.id) { change, over ->
                    if (over > 0f && scrollState.value == 0) {
                        claimed = true
                        change.consume()
                    }
                } ?: return@awaitEachGesture
                if (!claimed) return@awaitEachGesture

                var crossed = false
                var total = slopChange.positionChange().y.coerceAtLeast(0f)
                scope.launch { offsetY.snapTo(total) }
                verticalDrag(slopChange.id) { change ->
                    total = (total + change.positionChange().y).coerceAtLeast(0f)
                    change.consume()
                    scope.launch { offsetY.snapTo(total) }
                    val nowCrossed = total >= thresholdPx
                    if (nowCrossed != crossed) {
                        crossed = nowCrossed
                        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                    }
                }
                if (total >= thresholdPx) {
                    currentOnDismiss()
                } else {
                    scope.launch { offsetY.animateTo(0f, settleSpec) }
                }
            }
        }
}
