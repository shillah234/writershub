package com.writershub.app.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isActivated: Boolean = false,
    var walletBalance: Double = 0.0,
    var totalWithdrawn: Double = 0.0,
    var totalEarnings: Double = 0.0,
    val completedTasks: MutableList<String> = mutableListOf(),
    val withdrawals: MutableList<Withdrawal> = mutableListOf(),
    val transactions: MutableList<Transaction> = mutableListOf(),
    // 👇 NEW REFERRAL FIELDS
    val referralCode: String = "",           // User's own unique code
    val referredBy: String = "",              // Who referred this user
    val referrals: MutableList<String> = mutableListOf(), // People this user referred
    val referralEarnings: Double = 0.0        // Total earned from referrals
)