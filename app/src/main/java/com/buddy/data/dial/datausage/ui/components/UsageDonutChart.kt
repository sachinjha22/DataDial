package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.ui.UsagePalette
import com.buddy.data.dial.datausage.util.formatBytesParts

@Composable
fun UsageDonutChart(
    mobileBytes: Long,
    wifiBytes: Long,
    modifier: Modifier = Modifier,
) {
    val total = (mobileBytes + wifiBytes).coerceAtLeast(1L)
    val mobileFraction = mobileBytes / total.toFloat()

    val animatedFraction = remember { Animatable(0f) }
    LaunchedEffect(mobileFraction) {
        animatedFraction.animateTo(mobileFraction, animationSpec = tween(900, easing = FastOutSlowInEasing))
    }

    Box(modifier = modifier.size(176.dp), contentAlignment = Alignment.Center) {
        Canvas(modifier = Modifier.size(176.dp)) {
            val strokeWidth = 26.dp.toPx()
            val diameter = size.minDimension - strokeWidth
            val topLeft = Offset((size.width - diameter) / 2f, (size.height - diameter) / 2f)
            val arcSize = Size(diameter, diameter)

            drawArc(
                color = UsagePalette.Wifi,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize,
            )
            drawArc(
                color = UsagePalette.Mobile,
                startAngle = -90f,
                sweepAngle = 360f * animatedFraction.value,
                useCenter = false,
                style = Stroke(strokeWidth, cap = StrokeCap.Round),
                topLeft = topLeft,
                size = arcSize,
            )
        }
        Box(contentAlignment = Alignment.Center) {
            val parts = formatBytesParts(mobileBytes + wifiBytes)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = parts.value,
                    color = UsagePalette.TextPrimary,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "${parts.unit} TOTAL",
                    color = UsagePalette.TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                )
            }
        }
    }
}
