package com.buddy.data.dial.datausage.ui

import androidx.compose.ui.graphics.Color

/**
 * Standalone dark palette for the usage screen. Kept independent of the app's
 * Material theme so the screen reads as a distinct "instrument panel" regardless
 * of system light/dark mode.
 */
object UsagePalette {
    val BackgroundTop = Color(0xFF0A0D1A)
    val BackgroundMid = Color(0xFF0D1120)
    val BackgroundBottom = Color(0xFF11142A)

    val GlassSurface = Color(0xFFFFFFFF)
    val GlassSurfaceAlpha = 0.06f
    val GlassBorderAlpha = 0.14f

    val TextPrimary = Color(0xFFF5F6FF)
    val TextSecondary = Color(0xFFA6ACC8)
    val TextMuted = Color(0xFF6E7496)

    val Mobile = Color(0xFFFF6B7A)
    val MobileSoft = Color(0xFFFF9EA8)
    val Wifi = Color(0xFF33E0C9)
    val WifiSoft = Color(0xFF8CF5E6)

    val Download = Color(0xFF6C8CFF)
    val Upload = Color(0xFFFFB454)

    /** Solid primary action / header-mark color — matches the reference's flat indigo button. */
    val Accent = Color(0xFF6C63FF)
    val Warning = Color(0xFFF5A623)
    val Error = Color(0xFFFF5D72)

    /** Tint used for the permission-required banner container/border. */
    val WarningBannerTint = Color(0xFFE0526B)
}
