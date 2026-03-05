package com.writershub.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.writershub.app.ui.navigation.AppNavigation
import com.writershub.app.ui.theme.WritershubTheme

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WritershubTheme {
                AppNavigation()
            }
        }
    }
}