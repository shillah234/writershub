package com.writershub.app.data.repository

import com.writershub.app.data.model.User
import com.writershub.app.data.model.Withdrawal
import com.writershub.app.data.model.WithdrawalStatus
import java.util.Date

object SessionManager {
    var currentUser: User? = null
        private set

    fun login(user: User) {
        currentUser = user
    }

    fun register(user: User) {
        currentUser = user
    }

    fun activateAccount() {
        currentUser = currentUser?.copy(isActivated = true)
    }

    fun logout() {
        currentUser = null
    }

    fun isUserActivated(): Boolean {
        return currentUser?.isActivated == true
    }

    fun addTaskReward(reward: Double, taskId: String) {
        currentUser?.let { user ->
            if (!user.completedTasks.contains(taskId)) {
                val newBalance = user.walletBalance + reward
                val newTotalEarnings = user.totalEarnings + reward
                user.completedTasks.add(taskId)

                currentUser = user.copy(
                    walletBalance = newBalance,
                    totalEarnings = newTotalEarnings
                )
            }
        }
    }

    // UPDATED withdraw function with phone number
    fun requestWithdrawal(amount: Double, phoneNumber: String): String {
        currentUser?.let { user ->
            // Check minimum amount (KES 1000)
            if (amount < 1000) {
                return "MINIMUM_FAILED"
            }

            // Check sufficient balance
            if (user.walletBalance < amount) {
                return "BALANCE_FAILED"
            }

            // Validate phone number
            if (!phoneNumber.matches(Regex("^(07|01)\\d{8}$"))) {
                return "INVALID_PHONE"
            }

            // Process withdrawal
            val newBalance = user.walletBalance - amount
            val newTotalWithdrawn = user.totalWithdrawn + amount

            // Create withdrawal record
            val withdrawal = Withdrawal(
                amount = amount,
                phoneNumber = phoneNumber,
                status = WithdrawalStatus.PENDING,
                requestDate = Date()
            )

            // Add to user's withdrawals list
            user.withdrawals.add(0, withdrawal) // Add to beginning

            currentUser = user.copy(
                walletBalance = newBalance,
                totalWithdrawn = newTotalWithdrawn,
                withdrawals = user.withdrawals
            )
            return "SUCCESS"
        }
        return "BALANCE_FAILED"
    }

    // Get withdrawal history
    fun getWithdrawalHistory(): List<Withdrawal> {
        return currentUser?.withdrawals ?: emptyList()
    }

    fun isTaskCompleted(taskId: String): Boolean {
        return currentUser?.completedTasks?.contains(taskId) == true
    }

    fun getCompletedTasksCount(): Int {
        return currentUser?.completedTasks?.size ?: 0
    }
}