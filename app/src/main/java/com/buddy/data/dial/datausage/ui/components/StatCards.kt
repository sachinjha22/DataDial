package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.ui.UsagePalette
import com.buddy.data.dial.datausage.util.formatBytes

@Composable
fun TransportStatCard(
    title: String,
    icon: ImageVector,
    accentColor: Color,
    downloadBytes: Long,
    uploadBytes: Long,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UsagePalette.GlassSurface.copy(alpha = UsagePalette.GlassSurfaceAlpha)),
        border = BorderStroke(1.dp, accentColor.copy(alpha = 0.25f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(accentColor.copy(alpha = 0.18f), CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(icon, contentDescription = null, tint = accentColor, modifier = Modifier.size(18.dp))
                }
                Spacer(modifier = Modifier.width(10.dp))
                Text(text = title, color = UsagePalette.TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 15.sp)
            }
            Spacer(modifier = Modifier.height(12.dp))
            AnimatedByteText(
                bytes = downloadBytes + uploadBytes,
                color = UsagePalette.TextPrimary,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
            )
            Spacer(modifier = Modifier.height(10.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                DirectionalStat(icon = Icons.Filled.ArrowDownward, bytes = downloadBytes, color = UsagePalette.Download)
                Spacer(modifier = Modifier.width(14.dp))
                DirectionalStat(icon = Icons.Filled.ArrowUpward, bytes = uploadBytes, color = UsagePalette.Upload)
            }
        }
    }
}

@Composable
private fun DirectionalStat(icon: ImageVector, bytes: Long, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(12.dp))
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = formatBytes(bytes), color = UsagePalette.TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Medium)
    }
}

/** Small colored-dot legend entry, e.g. "Mobile  6.2 GB". Shared by the donut legend and the download/upload legend. */
@Composable
fun LegendDot(label: String, color: Color, bytes: Long, modifier: Modifier = Modifier) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .background(color, CircleShape),
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(text = label, color = UsagePalette.TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(text = formatBytes(bytes), color = UsagePalette.TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
    }
}

@Composable
fun DownloadUploadBar(
    downloadBytes: Long,
    uploadBytes: Long,
    modifier: Modifier = Modifier,
) {
    val total = (downloadBytes + uploadBytes).coerceAtLeast(1L)
    val downloadFraction = downloadBytes / total.toFloat()
    val animatedFraction by animateFloatAsState(
        targetValue = downloadFraction,
        animationSpec = tween(900),
        label = "downloadFraction",
    )
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = "DOWNLOAD VS UPLOAD",
            color = UsagePalette.TextMuted,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
        )
        Spacer(modifier = Modifier.height(10.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
                .clip(RoundedCornerShape(50)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(animatedFraction.coerceIn(0.001f, 0.999f))
                    .background(UsagePalette.Download),
            )
            Box(
                modifier = Modifier
                    .fillMaxHeight()
                    .weight((1f - animatedFraction).coerceIn(0.001f, 0.999f))
                    .background(UsagePalette.Upload),
            )
        }
        Spacer(modifier = Modifier.height(10.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(20.dp)) {
            LegendDot(label = "Download", color = UsagePalette.Download, bytes = downloadBytes)
            LegendDot(label = "Upload", color = UsagePalette.Upload, bytes = uploadBytes)
        }
    }
}

@Composable
fun AnimatedByteText(
    bytes: Long,
    color: Color,
    fontSize: androidx.compose.ui.unit.TextUnit,
    fontWeight: FontWeight? = null,
    modifier: Modifier = Modifier,
) {
    val formatted = formatBytes(bytes)
    AnimatedContent(
        targetState = formatted,
        transitionSpec = {
            (slideInVertically { it / 2 } + androidx.compose.animation.fadeIn()) togetherWith
                (slideOutVertically { -it / 2 } + androidx.compose.animation.fadeOut())
        },
        label = "byteText",
        modifier = modifier,
    ) { text ->
        Text(text = text, color = color, fontSize = fontSize, fontWeight = fontWeight)
    }
}
