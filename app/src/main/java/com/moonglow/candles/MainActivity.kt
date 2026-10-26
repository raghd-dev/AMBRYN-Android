package com.moonglow.candles

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.moonglow.candles.core.navigation.MoonGlowApp
import com.moonglow.candles.core.theme.MoonGlowTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MoonGlowTheme {
                MoonGlowApp()
            }
        }
    }
}