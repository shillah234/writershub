package com.writershub.app.data.model

data class User(
    val id: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val username: String = "",
    val email: String = "",
    val phone: String = "",
    val isActivated: Boolean = false,
    var walletBalance: Double = 0.0,
    var totalWithdrawn: Double = 0.0,
    var totalEarnings: Double = 0.0,
    val completedTasks: MutableList<String> = mutableListOf(),
    val withdrawals: MutableList<Withdrawal> = mutableListOf(),
    val transactions: MutableList<Transaction> = mutableListOf(),
    val referralCode: String = "",
    val referredBy: String = "",
    val referrals: MutableList<String> = mutableListOf(),
    val referralEarnings: Double = 0.0
)