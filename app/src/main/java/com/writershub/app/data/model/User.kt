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
    val withdrawals: MutableList<Withdrawal> = mutableListOf(), // 👈 ADD COMMA HERE
    val transactions: MutableList<Transaction> = mutableListOf()
)