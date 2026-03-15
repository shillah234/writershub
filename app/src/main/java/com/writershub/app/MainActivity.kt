package com.writershub.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.writershub.app.ui.navigation.AppNavigation
import com.writershub.app.ui.theme.WritershubTheme
import com.writershub.app.data.utils.DeepLinkManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    companion object {
        var deepLinkReferralCode: String? = null
    }

    private lateinit var prefs: SharedPreferences
    private var isReady = false

    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen before super.onCreate()
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)

        // Keep splash screen visible until isReady becomes true
        splashScreen.setKeepOnScreenCondition { !isReady }

        // Initialize SharedPreferences
        prefs = getSharedPreferences("writershub_prefs", Context.MODE_PRIVATE)

        // Check for deep link referral code
        deepLinkReferralCode = DeepLinkManager.extractReferralCode(intent)

        // If not from deep link, check SharedPreferences (for web fallback)
        if (deepLinkReferralCode.isNullOrEmpty()) {
            deepLinkReferralCode = prefs.getString("pending_referral", null)
            // Clear after reading so it's not used again
            if (deepLinkReferralCode != null) {
                prefs.edit().remove("pending_referral").apply()
            }
        }

        // Simulate initial loading (splash screen visible for at least 1.5 seconds)
        lifecycleScope.launch {
            // Load any necessary data here
            delay(1500) // Show splash for 1.5 seconds

            // Mark as ready - splash screen will now dismiss
            isReady = true
        }

        // Debug log
        if (deepLinkReferralCode != null) {
            println("✅ Referral code found: $deepLinkReferralCode")
        }

        setContent {
            WritershubTheme {
                AppNavigation()
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        // Check for referral code in new intent
        val newCode = DeepLinkManager.extractReferralCode(intent)
        if (newCode != null) {
            deepLinkReferralCode = newCode
            println("✅ New intent with referral code: $newCode")
        }
    }

    // Function to save referral code from web page
    fun saveReferralFromWeb(code: String) {
        prefs.edit().putString("pending_referral", code).apply()
        println("✅ Saved referral code from web: $code")
    }
}