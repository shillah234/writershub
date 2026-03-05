package com.writershub.app.data.model

data class User(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val phone: String = "",
    val isActivated: Boolean = false,
    val walletBalance: Double = 0.0,
    val totalWithdrawn: Double = 0.0,
    val totalEarnings: Double = 0.0
)