package com.buddy.data.dial.datausage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SignalCellularAlt
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.model.UsageResult
import com.buddy.data.dial.datausage.ui.UsagePalette
import com.buddy.data.dial.datausage.util.formatDateRange

@Composable
fun ResultsSection(
    result: UsageResult,
    startMillis: Long,
    endMillis: Long,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth()) {
        EntranceAnimated(index = 0) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = UsagePalette.GlassSurface.copy(alpha = UsagePalette.GlassSurfaceAlpha)),
                border = BorderStroke(1.dp, UsagePalette.GlassSurface.copy(alpha = UsagePalette.GlassBorderAlpha)),
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text = "TOTAL DEVICE USAGE",
                        color = UsagePalette.TextMuted,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = formatDateRange(startMillis, endMillis),
                        color = UsagePalette.TextMuted,
                        fontSize = 11.sp,
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    UsageDonutChart(
                        mobileBytes = result.mobile.totalBytes,
                        wifiBytes = result.wifi.totalBytes,
                    )
                    Spacer(modifier = Modifier.height(18.dp))
                    Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                        LegendDot(label = "Mobile", color = UsagePalette.Mobile, bytes = result.mobile.totalBytes)
                        LegendDot(label = "Wi-Fi", color = UsagePalette.Wifi, bytes = result.wifi.totalBytes)
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    DownloadUploadBar(
                        downloadBytes = result.totalDownloadBytes,
                        uploadBytes = result.totalUploadBytes,
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        EntranceAnimated(index = 1) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TransportStatCard(
                    title = "Mobile",
                    icon = Icons.Filled.SignalCellularAlt,
                    accentColor = UsagePalette.Mobile,
                    downloadBytes = result.mobile.rxBytes,
                    uploadBytes = result.mobile.txBytes,
                    modifier = Modifier.weight(1f),
                )
                Spacer(modifier = Modifier.width(12.dp))
                TransportStatCard(
                    title = "Wi-Fi",
                    icon = Icons.Filled.Wifi,
                    accentColor = UsagePalette.Wifi,
                    downloadBytes = result.wifi.rxBytes,
                    uploadBytes = result.wifi.txBytes,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
