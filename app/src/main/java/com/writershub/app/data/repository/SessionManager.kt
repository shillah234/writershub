package com.writershub.app.data.repository

import com.writershub.app.data.model.User

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

    fun withdrawMoney(amount: Double): String {
        currentUser?.let { user ->
            // Check minimum amount (KES 1000)
            if (amount < 1000) {
                return "MINIMUM_FAILED"
            }

            // Check sufficient balance
            if (user.walletBalance < amount) {
                return "BALANCE_FAILED"
            }

            // Process withdrawal
            val newBalance = user.walletBalance - amount
            val newTotalWithdrawn = user.totalWithdrawn + amount

            currentUser = user.copy(
                walletBalance = newBalance,
                totalWithdrawn = newTotalWithdrawn
            )
            return "SUCCESS"
        }
        return "BALANCE_FAILED"
    }

    fun isTaskCompleted(taskId: String): Boolean {
        return currentUser?.completedTasks?.contains(taskId) == true
    }

    fun getCompletedTasksCount(): Int {
        return currentUser?.completedTasks?.size ?: 0
    }
}