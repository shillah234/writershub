package com.writershub.app.data.utils

import kotlin.random.Random

object ReferralCodeGenerator {

    // Generate a random 6-character code (letters + numbers)
    // Examples: X7K9M2, 4H8J1P, A3B6C9
    fun generateReferralCode(): String {
        val characters = ('A'..'Z') + ('0'..'9')
        return (1..6)
            .map { characters.random() }
            .joinToString("")
    }

    // Generate a code with a prefix for branding
    fun generatePrefixedReferralCode(): String {
        val randomPart = generateReferralCode()
        return "WH-$randomPart"
    }

    // For backward compatibility - keep the old method if needed
    @Deprecated("Use generateReferralCode() instead", ReplaceWith("generateReferralCode()"))
    fun generateCode(username: String): String {
        return generateReferralCode()
    }

    fun generateSecureCode(): String {
        return generateReferralCode()
    }
}