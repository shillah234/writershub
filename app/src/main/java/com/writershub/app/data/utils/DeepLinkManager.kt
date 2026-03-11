package com.writershub.app.data.utils

import android.content.Intent
import android.net.Uri

object DeepLinkManager {

    // Your GitHub Pages URL
    private const val DOMAIN = "https://shillah234.github.io/-writershub-landing"

    fun generateReferralLink(referralCode: String): String {
        return "$DOMAIN/$referralCode"
    }

    // 👇 NEW: Extract from custom scheme (writershub://refer/CODE)
    fun extractReferralCodeFromCustomScheme(intent: Intent?): String? {
        if (intent == null) return null

        val data: Uri? = intent.data
        if (data == null) return null

        // Check if it's our custom scheme
        if (data.scheme == "writershub" && data.host == "refer") {
            // Get the path (e.g., /CODE123) and remove the leading /
            val path = data.path ?: return null
            return path.substringAfter("/")
        }

        return null
    }

    // 👇 UPDATED: Main extract function that checks both methods
    fun extractReferralCode(intent: Intent?): String? {
        if (intent == null) return null

        // First try custom scheme
        val customCode = extractReferralCodeFromCustomScheme(intent)
        if (customCode != null) return customCode

        // Then try web links
        val data: Uri? = intent.data
        if (data == null) return null

        // Check if it's our GitHub Pages domain
        if (data.scheme == "https" && data.host == "shillah234.github.io") {
            val path = data.path ?: return null
            // Extract the code (it's the last part after the final /)
            return path.substringAfterLast("/")
        }

        return null
    }

    // For reading from SharedPreferences (when app installed from Play Store)
    fun getReferralCodeFromPrefs(): String? {
        // This will be called from MainActivity
        return null // Placeholder - we'll implement in MainActivity
    }

    // 👇 NEW: Alternative safe version using let
    fun extractReferralCodeSafe(intent: Intent?): String? {
        return intent?.data?.let { uri ->
            when {
                uri.scheme == "writershub" && uri.host == "refer" ->
                    uri.path?.substringAfter("/")
                uri.scheme == "https" && uri.host == "shillah234.github.io" ->
                    uri.path?.substringAfterLast("/")
                else -> null
            }
        }
    }
}