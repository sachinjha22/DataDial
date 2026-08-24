package com.buddy.data.dial.datausage.mvi

import com.buddy.data.dial.datausage.model.UsageResult

data class UsageState(
    val startDateTimeMillis: Long,
    val endDateTimeMillis: Long,
    val hasUsageAccess: Boolean,
    val isCalculating: Boolean = false,
    val result: UsageResult? = null,
    val errorMessage: String? = null,
)

sealed interface UsageIntent {
    data class ChangeStartDateTime(val millis: Long) : UsageIntent
    data class ChangeEndDateTime(val millis: Long) : UsageIntent
    data object Calculate : UsageIntent
    data object RefreshPermissionState : UsageIntent
    data object RequestUsageAccess : UsageIntent
    data object DismissError : UsageIntent
}

sealed interface UsageEffect {
    data object LaunchUsageAccessSettings : UsageEffect
}
