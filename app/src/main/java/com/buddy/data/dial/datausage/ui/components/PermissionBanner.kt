package com.buddy.data.dial.datausage.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.ui.UsagePalette

@Composable
fun PermissionBanner(
    onGrantClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = UsagePalette.WarningBannerTint.copy(alpha = 0.12f)),
        border = BorderStroke(1.dp, UsagePalette.WarningBannerTint.copy(alpha = 0.35f)),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Filled.Lock, contentDescription = null, tint = UsagePalette.Warning)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Usage access required",
                    color = UsagePalette.TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 15.sp,
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Grant Usage Access so DataDial can read your device's network statistics. " +
                    "Nothing ever leaves your phone.",
                color = UsagePalette.TextSecondary,
                fontSize = 13.sp,
            )
            Spacer(modifier = Modifier.height(12.dp))
            Button(
                onClick = onGrantClick,
                colors = ButtonDefaults.buttonColors(containerColor = UsagePalette.Warning, contentColor = UsagePalette.BackgroundTop),
                shape = RoundedCornerShape(50),
            ) {
                Text("Grant Access", fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
