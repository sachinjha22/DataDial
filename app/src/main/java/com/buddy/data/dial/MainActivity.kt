package com.buddy.data.dial

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.toArgb
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.buddy.data.dial.datausage.ui.DataUsageScreen
import com.buddy.data.dial.datausage.ui.UsagePalette
import com.buddy.data.dial.ui.theme.DataDialTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        installSplashScreen()
        super.onCreate(savedInstanceState)
        val barColor = UsagePalette.BackgroundTop.toArgb()
        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.dark(barColor),
            navigationBarStyle = SystemBarStyle.dark(barColor),
        )
        setContent {
            DataDialTheme {
                DataUsageScreen()
            }
        }
    }
}
