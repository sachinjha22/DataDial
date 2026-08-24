package com.buddy.data.dial.datausage.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.buddy.data.dial.datausage.ui.UsagePalette
import com.buddy.data.dial.datausage.util.formatDateTime
import androidx.compose.ui.draw.scale
import java.util.Calendar
import java.util.TimeZone
import androidx.compose.foundation.clickable

private data class PendingDate(val year: Int, val month: Int, val day: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateTimeSelectorCard(
    label: String,
    icon: ImageVector,
    millis: Long,
    accentColor: Color,
    onChange: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showDatePicker by rememberSaveable { mutableStateOf(false) }
    var showTimePicker by rememberSaveable { mutableStateOf(false) }
    var pendingDate by remember { mutableStateOf<PendingDate?>(null) }

    val pressedScale by animateFloatAsState(
        targetValue = if (showDatePicker) 0.98f else 1f,
        animationSpec = spring(dampingRatio = 0.6f),
        label = "cardScale",
    )

    Card(
        modifier = modifier
            .fillMaxWidth()
            .scale(pressedScale)
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null) {
                showDatePicker = true
            },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = UsagePalette.GlassSurface.copy(alpha = UsagePalette.GlassSurfaceAlpha)),
        border = BorderStroke(1.dp, UsagePalette.GlassSurface.copy(alpha = UsagePalette.GlassBorderAlpha)),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier
                    .size(42.dp)
                    .background(accentColor.copy(alpha = 0.18f), CircleShape),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(icon, contentDescription = null, tint = accentColor)
            }
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = label, color = UsagePalette.TextMuted, fontSize = 12.sp)
                Text(
                    text = formatDateTime(millis),
                    color = UsagePalette.TextPrimary,
                    fontSize = 16.sp,
                )
            }
            Icon(
                imageVector = Icons.Filled.ChevronRight,
                contentDescription = null,
                tint = UsagePalette.TextMuted,
            )
        }
    }

    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = millis)
        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val selected = datePickerState.selectedDateMillis
                    if (selected != null) {
                        val utcCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = selected }
                        pendingDate = PendingDate(
                            year = utcCal.get(Calendar.YEAR),
                            month = utcCal.get(Calendar.MONTH),
                            day = utcCal.get(Calendar.DAY_OF_MONTH),
                        )
                        showDatePicker = false
                        showTimePicker = true
                    } else {
                        showDatePicker = false
                    }
                }) { Text("Next") }
            },
            dismissButton = { TextButton(onClick = { showDatePicker = false }) { Text("Cancel") } },
        ) {
            DatePicker(state = datePickerState)
        }
    }

    if (showTimePicker) {
        val referenceCal = remember { Calendar.getInstance().apply { timeInMillis = millis } }
        val timePickerState = rememberTimePickerState(
            initialHour = referenceCal.get(Calendar.HOUR_OF_DAY),
            initialMinute = referenceCal.get(Calendar.MINUTE),
            is24Hour = false,
        )
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    val date = pendingDate
                    if (date != null) {
                        val resultCal = Calendar.getInstance().apply {
                            clear()
                            set(date.year, date.month, date.day, timePickerState.hour, timePickerState.minute, 0)
                        }
                        onChange(resultCal.timeInMillis)
                    }
                    showTimePicker = false
                }) { Text("Set") }
            },
            dismissButton = { TextButton(onClick = { showTimePicker = false }) { Text("Cancel") } },
            text = { TimePicker(state = timePickerState) },
        )
    }
}
