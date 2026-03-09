package com.writershub.app.data.repository

import com.writershub.app.data.model.User
import com.writershub.app.data.model.Withdrawal
import com.writershub.app.data.auth.FirebaseAuthManager
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

object SessionManager {
    var currentUser: User? = null
        private set

    // Login with Firebase
    suspend fun login(email: String, password: String): Result<User> {
        return try {
            val result = FirebaseAuthManager.login(email, password)
            if (result.isSuccess) {
                currentUser = result.getOrNull()
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Register with Firebase
    suspend fun register(
        name: String,
        email: String,
        phone: String,
        password: String
    ): Result<User> {
        return try {
            val result = FirebaseAuthManager.signUp(email, password, name, phone)
            if (result.isSuccess) {
                currentUser = result.getOrNull()
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Activate account
    fun activateAccount() {
        currentUser = currentUser?.copy(isActivated = true)
        // Update in Firebase
        currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseAuthManager.updateUser(user)
            }
        }
    }

    // Logout
    fun logout() {
        currentUser = null
        FirebaseAuthManager.logout()
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

                // Update in Firebase
                CoroutineScope(Dispatchers.IO).launch {
                    FirebaseAuthManager.updateUser(currentUser!!)
                }
            }
        }
    }

    fun requestWithdrawal(amount: Double, phoneNumber: String): String {
        currentUser?.let { user ->
            if (amount < 1000) return "MINIMUM_FAILED"
            if (user.walletBalance < amount) return "BALANCE_FAILED"
            if (!phoneNumber.matches(Regex("^(07|01)\\d{8}$"))) return "INVALID_PHONE"

            val newBalance = user.walletBalance - amount
            val newTotalWithdrawn = user.totalWithdrawn + amount

            val withdrawal = Withdrawal(
                amount = amount,
                phoneNumber = phoneNumber,
                status = com.writershub.app.data.model.WithdrawalStatus.PENDING,
                requestDate = java.util.Date()
            )

            user.withdrawals.add(0, withdrawal)

            currentUser = user.copy(
                walletBalance = newBalance,
                totalWithdrawn = newTotalWithdrawn,
                withdrawals = user.withdrawals
            )

            // Update in Firebase
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseAuthManager.updateUser(currentUser!!)
            }

            return "SUCCESS"
        }
        return "BALANCE_FAILED"
    }

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