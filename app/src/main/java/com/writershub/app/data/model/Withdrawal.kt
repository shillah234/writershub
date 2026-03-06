package com.writershub.app.data.model

import java.util.Date

enum class WithdrawalStatus {
    PENDING,      // Withdrawal requested, waiting to be processed
    DISBURSED,    // Money sent to M-Pesa
    FAILED        // Withdrawal failed
}

data class Withdrawal(
    val id: String = System.currentTimeMillis().toString(),
    val amount: Double = 0.0,
    val phoneNumber: String = "",
    val status: WithdrawalStatus = WithdrawalStatus.PENDING,
    val requestDate: Date = Date(),
    val processedDate: Date? = null,
    val mpesaReference: String? = null
)