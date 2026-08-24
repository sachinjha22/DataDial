package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.ui.UsagePalette

@Composable
private fun rememberPulseAlpha(): State<Float> {
    val transition = rememberInfiniteTransition(label = "skeletonPulse")
    return transition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.85f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulseAlpha",
    )
}

/** Shown before the first calculation: a dashed ring and a short instruction. */
@Composable
fun EmptyUsagePlaceholder(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Canvas(modifier = Modifier.size(96.dp)) {
            drawCircle(
                color = UsagePalette.TextMuted.copy(alpha = 0.45f),
                radius = size.minDimension / 2f - 3.dp.toPx(),
                style = Stroke(width = 2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f))),
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "Select a start and end time, then calculate to see your usage breakdown.",
            color = UsagePalette.TextMuted,
            fontSize = 13.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 24.dp),
        )
    }
}

/** Shown while a calculation is in flight: a dashed donut skeleton plus skeleton bar/cards. */
@Composable
fun LoadingUsagePlaceholder(modifier: Modifier = Modifier) {
    val pulseAlpha by rememberPulseAlpha()
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Reading network statistics on-device…",
            color = UsagePalette.TextMuted,
            fontSize = 12.sp,
        )
        Spacer(modifier = Modifier.height(20.dp))
        Canvas(
            modifier = Modifier
                .size(176.dp)
                .alpha(pulseAlpha),
        ) {
            val strokeWidth = 26.dp.toPx()
            drawCircle(
                color = UsagePalette.TextMuted,
                radius = size.minDimension / 2f - strokeWidth / 2f,
                style = Stroke(width = strokeWidth, pathEffect = PathEffect.dashPathEffect(floatArrayOf(18f, 14f))),
            )
        }
        Spacer(modifier = Modifier.height(20.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .alpha(pulseAlpha)
                .clip(RoundedCornerShape(50))
                .background(UsagePalette.TextMuted.copy(alpha = 0.3f)),
        )
        Spacer(modifier = Modifier.height(14.dp))
        Row(modifier = Modifier.fillMaxWidth()) {
            SkeletonCard(modifier = Modifier.weight(1f).alpha(pulseAlpha))
            Spacer(modifier = Modifier.width(12.dp))
            SkeletonCard(modifier = Modifier.weight(1f).alpha(pulseAlpha))
        }
    }
}

@Composable
private fun SkeletonCard(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .height(112.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(UsagePalette.GlassSurface.copy(alpha = 0.08f)),
    )
}
