package com.buddy.data.dial.datausage.mvi

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.buddy.data.dial.datausage.data.NetworkUsageRepository
import com.buddy.data.dial.datausage.util.startOfToday
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsageViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = NetworkUsageRepository(application)

    private val _state = MutableStateFlow(
        UsageState(
            startDateTimeMillis = startOfToday(),
            endDateTimeMillis = System.currentTimeMillis(),
            hasUsageAccess = repository.hasUsageAccess(),
        )
    )
    val state: StateFlow<UsageState> = _state.asStateFlow()

    private val _effects = Channel<UsageEffect>(Channel.BUFFERED)
    val effects: Flow<UsageEffect> = _effects.receiveAsFlow()

    fun onIntent(intent: UsageIntent) {
        when (intent) {
            is UsageIntent.ChangeStartDateTime ->
                _state.update { it.copy(startDateTimeMillis = intent.millis, errorMessage = null) }

            is UsageIntent.ChangeEndDateTime ->
                _state.update { it.copy(endDateTimeMillis = intent.millis, errorMessage = null) }

            UsageIntent.Calculate -> calculate()

            UsageIntent.RefreshPermissionState ->
                _state.update { it.copy(hasUsageAccess = repository.hasUsageAccess()) }

            UsageIntent.RequestUsageAccess ->
                viewModelScope.launch { _effects.send(UsageEffect.LaunchUsageAccessSettings) }

            UsageIntent.DismissError -> _state.update { it.copy(errorMessage = null) }
        }
    }

    private fun calculate() {
        val current = _state.value
        if (current.startDateTimeMillis >= current.endDateTimeMillis) {
            _state.update { it.copy(errorMessage = "Start must be before the end time") }
            return
        }
        if (!repository.hasUsageAccess()) {
            _state.update { it.copy(hasUsageAccess = false, errorMessage = "Usage access permission is required") }
            return
        }
        viewModelScope.launch {
            _state.update { it.copy(isCalculating = true, errorMessage = null) }
            val outcome = repository.queryUsage(current.startDateTimeMillis, current.endDateTimeMillis)
            outcome.fold(
                onSuccess = { result -> _state.update { it.copy(isCalculating = false, result = result) } },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isCalculating = false,
                            errorMessage = error.message ?: "Failed to read usage statistics",
                        )
                    }
                },
            )
        }
    }
}
