package com.app.wardove.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LoadingIndicator
import androidx.compose.material3.LoadingIndicatorDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * M3 Expressive morphing-shape loading indicator, used everywhere the app
 * previously showed a plain circular spinner.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WardoveLoadingIndicator(
    modifier: Modifier = Modifier,
    size: Dp = LoadingIndicatorDefaults.ContainerWidth,
    color: Color = MaterialTheme.colorScheme.primary
) {
    LoadingIndicator(modifier = modifier.size(size), color = color)
}

/** Fills [modifier]'s bounds and centers a [WardoveLoadingIndicator] inside. */
@Composable
fun LoadingBox(modifier: Modifier = Modifier) {
    Box(modifier = modifier, contentAlignment = Alignment.Center) {
        WardoveLoadingIndicator()
    }
}

/** Compact indicator sized to sit inside a button in place of its label. */
@Composable
fun ButtonLoadingIndicator(color: Color = MaterialTheme.colorScheme.onPrimary) {
    WardoveLoadingIndicator(size = 24.dp, color = color)
}
