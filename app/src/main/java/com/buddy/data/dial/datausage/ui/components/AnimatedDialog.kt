package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.delay

private const val DIALOG_ENTER_DURATION_MS = 220
private const val DIALOG_EXIT_DURATION_MS = 160
private const val DIALOG_SCALE_FROM = 0.92f

/**
 * A dialog whose content fades + scales in on open and fades + scales out on close, instead of
 * the platform's abrupt default pop. The window is kept alive for [DIALOG_EXIT_DURATION_MS]
 * after [visible] flips to false so the exit transition has time to finish before the dialog
 * is actually torn down.
 */
@Composable
fun AnimatedDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    var showDialog by remember { mutableStateOf(visible) }
    var contentVisible by remember { mutableStateOf(false) }

    LaunchedEffect(visible) {
        if (visible) {
            showDialog = true
            contentVisible = true
        } else {
            contentVisible = false
            delay(DIALOG_EXIT_DURATION_MS.toLong())
            showDialog = false
        }
    }

    if (showDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false),
        ) {
            AnimatedVisibility(
                visible = contentVisible,
                enter = fadeIn(tween(DIALOG_ENTER_DURATION_MS, easing = FastOutSlowInEasing)) +
                    scaleIn(
                        initialScale = DIALOG_SCALE_FROM,
                        animationSpec = tween(DIALOG_ENTER_DURATION_MS, easing = FastOutSlowInEasing),
                    ),
                exit = fadeOut(tween(DIALOG_EXIT_DURATION_MS, easing = FastOutSlowInEasing)) +
                    scaleOut(
                        targetScale = DIALOG_SCALE_FROM,
                        animationSpec = tween(DIALOG_EXIT_DURATION_MS, easing = FastOutSlowInEasing),
                    ),
            ) {
                Surface(
                    modifier = modifier.padding(24.dp),
                    shape = RoundedCornerShape(28.dp),
                    color = MaterialTheme.colorScheme.surfaceContainerHigh,
                    tonalElevation = 6.dp,
                ) {
                    Column(content = content)
                }
            }
        }
    }
}
