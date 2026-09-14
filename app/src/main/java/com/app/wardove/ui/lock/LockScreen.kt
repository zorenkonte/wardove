package com.app.wardove.ui.lock

import androidx.activity.compose.BackHandler
import androidx.activity.compose.LocalActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.app.wardove.R
import com.app.wardove.ui.components.WardoveLottie
import com.composables.icons.lucide.Fingerprint
import com.composables.icons.lucide.Lucide

/**
 * Full-screen lock overlay drawn above the NavHost.
 *
 * It must be a hard barrier, not just a visual one:
 *  - every pointer event is consumed here so taps can't reach the screen behind it;
 *  - the system back button sends the task to the background instead of popping
 *    the (hidden) navigation back stack.
 */
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun LockScreen(onBiometricRequest: () -> Unit) {
    val activity = LocalActivity.current
    BackHandler { activity?.moveTaskToBack(true) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                awaitPointerEventScope {
                    while (true) {
                        awaitPointerEvent().changes.forEach { it.consume() }
                    }
                }
            },
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(20.dp),
            modifier = Modifier.padding(horizontal = 32.dp)
        ) {
            Surface(
                shape = MaterialTheme.shapes.extraLarge,
                color = MaterialTheme.colorScheme.surfaceContainer,
                modifier = Modifier.size(180.dp)
            ) {
                WardoveLottie(
                    animation = R.raw.lottie_private,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp)
                )
            }
            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = stringResource(R.string.lock_subtitle),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Spacer(Modifier.height(4.dp))
            Button(
                onClick = onBiometricRequest,
                shapes = ButtonDefaults.shapes(),
                contentPadding = ButtonDefaults.MediumContentPadding,
                modifier = Modifier.height(ButtonDefaults.MediumContainerHeight)
            ) {
                Icon(
                    imageVector = Lucide.Fingerprint,
                    contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.MediumIconSize)
                )
                Spacer(Modifier.width(ButtonDefaults.MediumIconSpacing))
                Text(
                    stringResource(R.string.lock_unlock_button),
                    style = ButtonDefaults.textStyleFor(ButtonDefaults.MediumContainerHeight)
                )
            }
        }
    }
}
