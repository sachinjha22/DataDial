package com.buddy.data.dial.datausage.ui

import android.content.Intent
import android.provider.Settings
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.EventAvailable
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.buddy.data.dial.datausage.model.NetworkUsage
import com.buddy.data.dial.datausage.model.UsageResult
import com.buddy.data.dial.datausage.mvi.UsageEffect
import com.buddy.data.dial.datausage.mvi.UsageIntent
import com.buddy.data.dial.datausage.mvi.UsageState
import com.buddy.data.dial.datausage.mvi.UsageViewModel
import com.buddy.data.dial.datausage.ui.components.CalculateButton
import com.buddy.data.dial.datausage.ui.components.DateTimeSelectorCard
import com.buddy.data.dial.datausage.ui.components.EmptyUsagePlaceholder
import com.buddy.data.dial.datausage.ui.components.LoadingUsagePlaceholder
import com.buddy.data.dial.datausage.ui.components.PermissionBanner
import com.buddy.data.dial.datausage.ui.components.ResultsSection
import com.buddy.data.dial.ui.theme.DataDialTheme

@Composable
fun DataUsageScreen() {
    val viewModel: UsageViewModel = viewModel()
    val state by viewModel.state.collectAsStateWithLifecycle()
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        viewModel.effects.collect { effect ->
            when (effect) {
                UsageEffect.LaunchUsageAccessSettings -> {
                    val intent = Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS).apply {
                        data = android.net.Uri.parse("package:${context.packageName}")
                    }
                    runCatching { context.startActivity(intent) }
                        .onFailure { context.startActivity(Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS)) }
                }
            }
        }
    }

    val lifecycleOwner = LocalLifecycleOwner.current
    LaunchedEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.onIntent(UsageIntent.RefreshPermissionState)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
    }

    DataUsageScreenContent(state = state, onIntent = viewModel::onIntent)
}

/**
 * Stateless rendering of the screen. Kept separate from [DataUsageScreen] so it can be driven
 * with fake [UsageState] values in @Preview — the real [DataUsageScreen] owns a ViewModel whose
 * init block calls into AppOpsManager/NetworkStatsManager, which aren't available in the
 * Preview renderer and would otherwise crash the preview.
 */
@Composable
fun DataUsageScreenContent(
    state: UsageState,
    onIntent: (UsageIntent) -> Unit,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(animatedBackgroundBrush()),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding(),
            contentPadding = PaddingValues(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item { ScreenHeader() }

            if (!state.hasUsageAccess) {
                item {
                    PermissionBanner(onGrantClick = { onIntent(UsageIntent.RequestUsageAccess) })
                }
            }

            item {
                DateTimeSelectorCard(
                    label = "START",
                    icon = Icons.Filled.CalendarMonth,
                    millis = state.startDateTimeMillis,
                    accentColor = UsagePalette.Download,
                    onChange = { onIntent(UsageIntent.ChangeStartDateTime(it)) },
                )
            }
            item {
                DateTimeSelectorCard(
                    label = "END",
                    icon = Icons.Filled.EventAvailable,
                    millis = state.endDateTimeMillis,
                    accentColor = UsagePalette.Upload,
                    onChange = { onIntent(UsageIntent.ChangeEndDateTime(it)) },
                )
            }

            item {
                AnimatedVisibility(
                    visible = state.errorMessage != null,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    state.errorMessage?.let { message ->
                        Text(
                            text = message,
                            color = UsagePalette.Error,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(horizontal = 4.dp),
                        )
                    }
                }
            }

            item {
                CalculateButton(
                    isLoading = state.isCalculating,
                    enabled = state.hasUsageAccess,
                    onClick = { onIntent(UsageIntent.Calculate) },
                )
            }

            item {
                val panel = when {
                    state.isCalculating -> UsagePanel.LOADING
                    state.result != null -> UsagePanel.RESULT
                    else -> UsagePanel.EMPTY
                }
                AnimatedContent(
                    targetState = panel,
                    transitionSpec = {
                        (fadeIn() + slideInVertically(initialOffsetY = { it / 8 })) togetherWith fadeOut()
                    },
                    label = "resultsPanel",
                ) { target ->
                    when (target) {
                        UsagePanel.EMPTY -> EmptyUsagePlaceholder()
                        UsagePanel.LOADING -> LoadingUsagePlaceholder()
                        UsagePanel.RESULT -> state.result?.let { result ->
                            ResultsSection(
                                result = result,
                                startMillis = state.startDateTimeMillis,
                                endMillis = state.endDateTimeMillis,
                            )
                        }
                    }
                }
            }

            item { Spacer(modifier = Modifier.height(24.dp)) }
        }
    }
}

@Composable
private fun ScreenHeader() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(40.dp)
                .background(UsagePalette.Accent, RoundedCornerShape(12.dp)),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.NetworkCheck,
                contentDescription = null,
                tint = androidx.compose.ui.graphics.Color.White,
                modifier = Modifier.size(22.dp),
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(
                text = "DataDial",
                color = UsagePalette.TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "On-device tracking · no cloud · no ads",
                color = UsagePalette.TextSecondary,
                fontSize = 12.sp
            )
        }
    }
}

private enum class UsagePanel { EMPTY, LOADING, RESULT }

@Composable
private fun animatedBackgroundBrush(): Brush {
    val transition = rememberInfiniteTransition(label = "bgTransition")
    val shift by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 9000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "bgShift",
    )
    return Brush.linearGradient(
        colors = listOf(
            UsagePalette.BackgroundTop,
            UsagePalette.BackgroundMid,
            UsagePalette.BackgroundBottom
        ),
        start = Offset(0f, 200f * shift),
        end = Offset(1200f * shift + 400f, 1600f),
    )
}

private fun previewState(
    hasUsageAccess: Boolean = true,
    isCalculating: Boolean = false,
    result: UsageResult? = null,
    errorMessage: String? = null,
): UsageState {
    val now = System.currentTimeMillis()
    return UsageState(
        startDateTimeMillis = now - 6L * 24 * 60 * 60 * 1000,
        endDateTimeMillis = now,
        hasUsageAccess = hasUsageAccess,
        isCalculating = isCalculating,
        result = result,
        errorMessage = errorMessage,
    )
}

private val previewResult = UsageResult(
    mobile = NetworkUsage(rxBytes = 5_476_083_302L, txBytes = 1_395_864_371L),
    wifi = NetworkUsage(rxBytes = 11_703_785_882L, txBytes = 1_503_238_554L),
)

@Preview(name = "Main — before first calculation", showBackground = true)
@Composable
private fun DataUsageScreenPreviewEmpty() {
    DataDialTheme {
        DataUsageScreenContent(state = previewState(), onIntent = {})
    }
}

@Preview(name = "Permission not granted", showBackground = true)
@Composable
private fun DataUsageScreenPreviewPermission() {
    DataDialTheme {
        DataUsageScreenContent(state = previewState(hasUsageAccess = false), onIntent = {})
    }
}

@Preview(name = "Loading", showBackground = true)
@Composable
private fun DataUsageScreenPreviewLoading() {
    DataDialTheme {
        DataUsageScreenContent(state = previewState(isCalculating = true), onIntent = {})
    }
}

@Preview(name = "Results", showBackground = true)
@Composable
private fun DataUsageScreenPreviewResults() {
    DataDialTheme {
        DataUsageScreenContent(state = previewState(result = previewResult), onIntent = {})
    }
}
