package com.writershub.app.data.repository

import com.writershub.app.data.model.User
import com.writershub.app.data.model.Withdrawal
import com.writershub.app.data.model.Transaction
import com.writershub.app.data.model.TransactionType
import com.writershub.app.data.model.Referral
import com.writershub.app.data.auth.FirebaseAuthManager
import com.writershub.app.data.utils.ReferralCodeGenerator
import com.writershub.app.data.repository.UsernameManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

object SessionManager {
    var currentUser: User? = null
        private set

    // Login with Firebase (email) - kept for backward compatibility
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

    // NEW: Login with username
    suspend fun loginWithUsername(username: String, password: String): Result<User> {
        return try {
            // First get UID from username
            val uid = UsernameManager.getUserIdFromUsername(username)
            if (uid == null) {
                return Result.failure(Exception("Username not found"))
            }

            // Get the email from user document
            val userDoc = FirebaseFirestore.getInstance()
                .collection("users")
                .document(uid)
                .get()
                .await()

            val email = userDoc.getString("email") ?: return Result.failure(Exception("User data not found"))

            // Now login with email
            val result = FirebaseAuthManager.login(email, password)
            if (result.isSuccess) {
                currentUser = result.getOrNull()
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // NEW: Register with username (uses atomic batch write)
    suspend fun registerWithUsername(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        referralCode: String? = null
    ): Result<User> {
        return try {
            val result = UsernameManager.registerUser(
                firstName = firstName,
                lastName = lastName,
                username = username,
                email = email,
                phone = phone,
                password = password,
                referralCode = referralCode
            )
            if (result.isSuccess) {
                currentUser = result.getOrNull()
            }
            result
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Original register function - kept for backward compatibility
    suspend fun register(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        referralCode: String? = null
    ): Result<User> {
        return registerWithUsername(
            firstName = firstName,
            lastName = lastName,
            username = username,
            email = email,
            phone = phone,
            password = password,
            referralCode = referralCode
        )
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

                // Add transaction record
                addTransaction(
                    type = TransactionType.TASK_EARNING,
                    amount = reward,
                    description = "Earned from task",
                    taskId = taskId
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
                requestDate = Date()
            )

            user.withdrawals.add(0, withdrawal)

            currentUser = user.copy(
                walletBalance = newBalance,
                totalWithdrawn = newTotalWithdrawn,
                withdrawals = user.withdrawals
            )

            // Add transaction record
            addTransaction(
                type = TransactionType.WITHDRAWAL,
                amount = amount,
                description = "Withdrawal to $phoneNumber",
                withdrawalId = withdrawal.id
            )

            // Update in Firebase
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseAuthManager.updateUser(currentUser!!)
            }

            return "SUCCESS"
        }
        return "BALANCE_FAILED"
    }

    // Add transaction function
    fun addTransaction(type: TransactionType, amount: Double, description: String, taskId: String? = null, withdrawalId: String? = null) {
        currentUser?.let { user ->
            val transaction = Transaction(
                id = System.currentTimeMillis().toString(),
                userId = user.id,
                type = type,
                amount = amount,
                description = description,
                date = Date(),
                taskId = taskId,
                withdrawalId = withdrawalId
            )

            user.transactions.add(0, transaction)
            currentUser = user.copy(transactions = user.transactions)

            // Update in Firebase
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseAuthManager.updateUser(currentUser!!)
            }
        }
    }

    // Get user's referral code
    fun getMyReferralCode(): String {
        return currentUser?.referralCode ?: ""
    }

    // Get number of people referred
    fun getReferralCount(): Int {
        return currentUser?.referrals?.size ?: 0
    }

    // Get total referral earnings
    fun getReferralEarnings(): Double {
        return currentUser?.referralEarnings ?: 0.0
    }

    // Get list of people referred
    fun getReferrals(): List<String> {
        return currentUser?.referrals ?: emptyList()
    }

    // Generate a new referral code (if needed)
    fun generateNewReferralCode(): String {
        val newCode = ReferralCodeGenerator.generateSecureCode()
        currentUser = currentUser?.copy(referralCode = newCode)

        // Update in Firebase
        currentUser?.let { user ->
            CoroutineScope(Dispatchers.IO).launch {
                FirebaseAuthManager.updateUser(user)
            }
        }

        return newCode
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