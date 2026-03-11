package com.writershub.app

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.writershub.app.ui.navigation.AppNavigation
import com.writershub.app.ui.theme.WritershubTheme
import com.writershub.app.data.utils.DeepLinkManager

class MainActivity : ComponentActivity() {

    companion object {
        var deepLinkReferralCode: String? = null
    }

    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        prefs = getSharedPreferences("writershub_prefs", Context.MODE_PRIVATE)

        deepLinkReferralCode = DeepLinkManager.extractReferralCode(intent)

        if (deepLinkReferralCode.isNullOrEmpty()) {
            deepLinkReferralCode = prefs.getString("pending_referral", null)
            if (deepLinkReferralCode != null) {
                prefs.edit().remove("pending_referral").apply()
            }
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
        deepLinkReferralCode = DeepLinkManager.extractReferralCode(intent)
    }
}