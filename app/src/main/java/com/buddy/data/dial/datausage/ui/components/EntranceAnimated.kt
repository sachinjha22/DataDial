package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private const val ENTRANCE_STAGGER_STEP_MS = 45L
private const val ENTRANCE_MAX_STAGGER_STEPS = 6
private const val ENTRANCE_DURATION_MS = 420
private const val ENTRANCE_START_OFFSET_DP = 22f

/**
 * Fade-in + slight upward slide played every time [content] enters composition, with a
 * per-item delay (capped so a long list doesn't feel sluggish) for a staggered reveal.
 * Because it's keyed on [LaunchedEffect(Unit)] scoped to this call site's composition
 * lifetime, it plays once per appearance — e.g. once on screen load for items that stay
 * mounted, or again each time a branch (like a fresh calculation result) re-enters
 * composition.
 */
@Composable
fun EntranceAnimated(index: Int, content: @Composable () -> Unit) {
    val alpha = remember { Animatable(0f) }
    val offsetY = remember { Animatable(ENTRANCE_START_OFFSET_DP) }
    LaunchedEffect(Unit) {
        delay(ENTRANCE_STAGGER_STEP_MS * index.coerceAtMost(ENTRANCE_MAX_STAGGER_STEPS))
        coroutineScope {
            launch { alpha.animateTo(1f, tween(ENTRANCE_DURATION_MS, easing = FastOutSlowInEasing)) }
            launch { offsetY.animateTo(0f, tween(ENTRANCE_DURATION_MS, easing = FastOutSlowInEasing)) }
        }
    }
    Box(
        modifier = Modifier
            .alpha(alpha.value)
            .offset(y = offsetY.value.dp),
    ) {
        content()
    }
}
