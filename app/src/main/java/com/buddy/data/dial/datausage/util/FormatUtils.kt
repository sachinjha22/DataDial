package com.buddy.data.dial.datausage.util

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

/** Human readable byte count, e.g. "482.30 MB". */
fun formatBytes(bytes: Long): String {
    if (bytes < 1024) return "$bytes B"
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes / 1024.0
    var unitIndex = 0
    while (value >= 1024.0 && unitIndex < units.size - 1) {
        value /= 1024.0
        unitIndex++
    }
    return String.format(Locale.US, "%.2f %s", value, units[unitIndex])
}

/** A byte count split into its numeric value and unit, e.g. "18.4" + "GB", for large display. */
data class ByteParts(val value: String, val unit: String)

fun formatBytesParts(bytes: Long): ByteParts {
    if (bytes < 1024) return ByteParts(bytes.toString(), "B")
    val units = arrayOf("KB", "MB", "GB", "TB")
    var value = bytes / 1024.0
    var unitIndex = 0
    while (value >= 1024.0 && unitIndex < units.size - 1) {
        value /= 1024.0
        unitIndex++
    }
    val formatted = if (value >= 100) String.format(Locale.US, "%.0f", value)
    else String.format(Locale.US, "%.1f", value)
    return ByteParts(formatted, units[unitIndex])
}

private val dateTimeFormat: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
    SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
}

private val shortDateFormat: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
    SimpleDateFormat("MMM d", Locale.getDefault())
}

private val shortDateWithYearFormat: ThreadLocal<SimpleDateFormat> = ThreadLocal.withInitial {
    SimpleDateFormat("MMM d, yyyy", Locale.getDefault())
}

fun formatDateTime(millis: Long): String = dateTimeFormat.get()!!.format(Date(millis))

/** e.g. "Aug 18 – Aug 24, 2026". */
fun formatDateRange(startMillis: Long, endMillis: Long): String {
    val start = shortDateFormat.get()!!.format(Date(startMillis))
    val end = shortDateWithYearFormat.get()!!.format(Date(endMillis))
    return "$start – $end"
}

fun startOfToday(): Long {
    val calendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.timeInMillis
}
