package com.buddy.data.dial.datausage.model

/** Download (rx) and upload (tx) totals for a single network transport over a time window. */
data class NetworkUsage(
    val rxBytes: Long,
    val txBytes: Long,
) {
    val totalBytes: Long get() = rxBytes + txBytes

    companion object {
        val ZERO = NetworkUsage(0L, 0L)
    }
}

/** Full breakdown for one calculation: mobile vs Wi-Fi, each split into download/upload. */
data class UsageResult(
    val mobile: NetworkUsage,
    val wifi: NetworkUsage,
) {
    val totalDownloadBytes: Long get() = mobile.rxBytes + wifi.rxBytes
    val totalUploadBytes: Long get() = mobile.txBytes + wifi.txBytes
    val totalBytes: Long get() = mobile.totalBytes + wifi.totalBytes
}
