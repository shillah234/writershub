package com.writershub.app.data.utils

import kotlin.random.Random

object ReferralCodeGenerator {

    // Generate code from username + 2 digits (for new users)
    fun generateCode(username: String): String {
        val usernamePart = username.lowercase()
        val randomPart = Random.nextInt(10, 99).toString()
        return "$usernamePart$randomPart"
    }

    // Generate a secure random code (6 chars, lowercase + numbers)
    fun generateSecureCode(): String {
        val chars = ('a'..'z') + ('0'..'9')
        return (1..6)
            .map { chars.random() }
            .joinToString("")
    }

    // Alternative: Generate code with custom length
    fun generateSecureCode(length: Int): String {
        val chars = ('a'..'z') + ('0'..'9')
        return (1..length)
            .map { chars.random() }
            .joinToString("")
    }
}