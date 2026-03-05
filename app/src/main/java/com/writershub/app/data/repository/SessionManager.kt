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

    // NEW: Add money to wallet when task is completed
    fun addTaskReward(reward: Double, taskId: String) {
        currentUser?.let { user ->
            // Check if task already completed
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

    // NEW: Withdraw money from wallet
    fun withdrawMoney(amount: Double): Boolean {
        currentUser?.let { user ->
            if (user.walletBalance >= amount && amount >= 50) { // Minimum withdrawal 50 KES
                val newBalance = user.walletBalance - amount
                val newTotalWithdrawn = user.totalWithdrawn + amount

                currentUser = user.copy(
                    walletBalance = newBalance,
                    totalWithdrawn = newTotalWithdrawn
                )
                return true
            }
        }
        return false
    }

    // NEW: Check if task is already completed
    fun isTaskCompleted(taskId: String): Boolean {
        return currentUser?.completedTasks?.contains(taskId) == true
    }

    // NEW: Get completed tasks count
    fun getCompletedTasksCount(): Int {
        return currentUser?.completedTasks?.size ?: 0
    }
}