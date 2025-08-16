package com.michaelbentz.stacksearch.presentation.activity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.michaelbentz.stacksearch.presentation.screen.MainScreen
import com.michaelbentz.stacksearch.presentation.theme.StackSearchTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val systemBarStyle = SystemBarStyle.light(
            scrim = Color.Transparent.toArgb(),
            darkScrim = Color.Transparent.toArgb(),
        )
        enableEdgeToEdge(
            statusBarStyle = systemBarStyle,
            navigationBarStyle = systemBarStyle,
        )
        setContent {
            StackSearchTheme {
                MainScreen()
            }
        }
    }
}
