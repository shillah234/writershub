package com.writershub.app.data.utils

import java.util.UUID

object ReferralCodeGenerator {

    fun generateCode(name: String): String {
        // Take first 3 letters of name (uppercase) + random 4 digits
        val namePart = name.take(3).uppercase()
        val randomPart = (1000..9999).random().toString()
        return "$namePart$randomPart"
    }

    fun generateSecureCode(): String {
        // Generate a random 8-character code
        return UUID.randomUUID().toString().take(8).uppercase()
    }
}