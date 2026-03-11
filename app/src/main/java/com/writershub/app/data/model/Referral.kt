package com.writershub.app.data.model

import java.util.Date

data class Referral(
    val id: String = "",
    val referrerId: String = "",      // User who referred
    val referredUserId: String = "",  // User who joined
    val referredUserName: String = "", // Name of person who joined
    val bonusAmount: Double = 20.0,    // Bonus earned (KES 20)
    val date: Date = Date(),
    val status: String = "PAID"        // PAID, PENDING
)