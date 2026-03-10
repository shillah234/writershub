package com.writershub.app.data.model

import java.util.Date

enum class TransactionType {
    TASK_EARNING,
    WITHDRAWAL,
    REFERRAL_BONUS,
    ACTIVATION_FEE
}

data class Transaction(
    val id: String = "",
    val userId: String = "",
    val type: TransactionType = TransactionType.TASK_EARNING,
    val amount: Double = 0.0,
    val description: String = "",
    val date: Date = Date(),
    val taskId: String? = null,
    val withdrawalId: String? = null
)