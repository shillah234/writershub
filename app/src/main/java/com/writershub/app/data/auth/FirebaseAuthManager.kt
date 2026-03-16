package com.writershub.app.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.User
import com.writershub.app.data.model.Withdrawal
import com.writershub.app.data.model.Transaction
import com.writershub.app.data.utils.ReferralCodeGenerator
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine
import java.util.Date

object FirebaseAuthManager {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val TAG = "FirebaseAuthManager"

    // Sign up with email and password - WITH USERNAME UNIQUENESS CHECK
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        username: String,
        phone: String,
        referralCode: String? = null
    ): Result<User> = suspendCoroutine { continuation ->

        // FIRST: Check if username exists (this works)
        firestore.collection("users")
            .whereEqualTo("username", username.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty()) {
                    continuation.resume(Result.failure(Exception("Username already taken. Please choose another.")))
                    return@addOnSuccessListener
                }

                // SECOND: Create Firebase Auth user
                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val firebaseUser = auth.currentUser

                            Log.d(TAG, "✅ Auth created for: ${firebaseUser?.uid}")

                            // Generate referral code
                            val userReferralCode = ReferralCodeGenerator.generateCode(username)

                            // Split name
                            val nameParts = name.split(" ")
                            val firstName = nameParts.firstOrNull() ?: ""
                            val lastName = if (nameParts.size > 1) nameParts.drop(1).joinToString(" ") else ""

                            val newUser = User(
                                id = firebaseUser?.uid ?: "",
                                firstName = firstName,
                                lastName = lastName,
                                username = username.lowercase(),
                                email = email,
                                phone = phone,
                                isActivated = false,
                                walletBalance = 0.0,
                                totalWithdrawn = 0.0,
                                totalEarnings = 0.0,
                                completedTasks = mutableListOf(),
                                withdrawals = mutableListOf(),
                                transactions = mutableListOf(),
                                referralCode = userReferralCode,
                                referredBy = referralCode ?: "",
                                referrals = mutableListOf(),
                                referralEarnings = 0.0
                            )

                            // THIRD: Wait a moment for auth to propagate
                            android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({
                                // FOURTH: Create Firestore document
                                firestore.collection("users")
                                    .document(newUser.id)
                                    .set(newUser)
                                    .addOnSuccessListener {
                                        Log.d(TAG, "✅ User saved to Firestore: ${newUser.id}")

                                        // Process referral bonus
                                        if (!referralCode.isNullOrEmpty()) {
                                            processReferralBonus(referralCode, newUser)
                                        }

                                        continuation.resume(Result.success(newUser))
                                    }
                                    .addOnFailureListener { e ->
                                        Log.e(TAG, "❌ Firestore error: ${e.message}")
                                        continuation.resume(Result.failure(e))
                                    }
                            }, 500) // 500ms delay

                        } else {
                            val errorMsg = task.exception?.message ?: "Registration failed"
                            Log.e(TAG, "❌ Auth error: $errorMsg")
                            continuation.resume(Result.failure(Exception(errorMsg)))
                        }
                    }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Username check error: ${e.message}")
                continuation.resume(Result.failure(e))
            }
    }

    // Process referral bonus when someone uses a referral code
    private fun processReferralBonus(referralCode: String, newUser: User) {
        // Find the user who owns this referral code
        firestore.collection("users")
            .whereEqualTo("referralCode", referralCode)
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty()) {
                    val referrerDoc = documents.first()
                    val referrer = referrerDoc.toObject(User::class.java)

                    if (referrer != null) {
                        // Add this new user to referrer's referrals list
                        val updatedReferrals = referrer.referrals.toMutableList()
                        updatedReferrals.add(newUser.id)

                        // Add referral bonus to referrer's wallet (KES 20)
                        val bonusAmount = 20.0
                        val newBalance = referrer.walletBalance + bonusAmount
                        val newReferralEarnings = referrer.referralEarnings + bonusAmount

                        // Create transaction record for referral bonus
                        val transaction = Transaction(
                            id = System.currentTimeMillis().toString(),
                            userId = referrer.id,
                            type = com.writershub.app.data.model.TransactionType.REFERRAL_BONUS,
                            amount = bonusAmount,
                            description = "Referral bonus from ${newUser.username}",
                            date = Date()
                        )

                        // Update referrer's transactions list
                        val updatedTransactions = referrer.transactions.toMutableList()
                        updatedTransactions.add(0, transaction)

                        // Update referrer in Firestore
                        firestore.collection("users")
                            .document(referrer.id)
                            .update(
                                mapOf(
                                    "referrals" to updatedReferrals,
                                    "walletBalance" to newBalance,
                                    "referralEarnings" to newReferralEarnings,
                                    "transactions" to updatedTransactions
                                )
                            )
                            .addOnSuccessListener {
                                Log.d(TAG, "Referral bonus paid to ${referrer.username}")
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error paying referral bonus", e)
                            }
                    }
                }
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error finding referrer", e)
            }
    }

    // Login with email and password
    suspend fun login(email: String, password: String): Result<User> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    try {
                                        val user = document.toObject(User::class.java)
                                        if (user != null) {
                                            // Ensure all fields exist (for old users)
                                            val completeUser = user.copy(
                                                transactions = user.transactions ?: mutableListOf(),
                                                referrals = user.referrals ?: mutableListOf(),
                                                referralEarnings = user.referralEarnings ?: 0.0,
                                                referralCode = user.referralCode ?: ReferralCodeGenerator.generateCode(user.username.ifEmpty { "user" }),
                                                firstName = user.firstName ?: "",
                                                lastName = user.lastName ?: "",
                                                username = user.username ?: ""
                                            )
                                            Log.d(TAG, "User loaded from Firestore: ${completeUser.id}")
                                            continuation.resume(Result.success(completeUser))
                                        } else {
                                            // Create default user
                                            val defaultUser = User(
                                                id = firebaseUser.uid,
                                                firstName = "",
                                                lastName = "",
                                                username = "",
                                                email = email,
                                                phone = "",
                                                isActivated = false,
                                                walletBalance = 0.0,
                                                totalWithdrawn = 0.0,
                                                totalEarnings = 0.0,
                                                completedTasks = mutableListOf(),
                                                withdrawals = mutableListOf(),
                                                transactions = mutableListOf(),
                                                referralCode = ReferralCodeGenerator.generateCode("user"),
                                                referredBy = "",
                                                referrals = mutableListOf(),
                                                referralEarnings = 0.0
                                            )
                                            continuation.resume(Result.success(defaultUser))
                                        }
                                    } catch (e: Exception) {
                                        Log.e(TAG, "Error converting user document", e)
                                        val defaultUser = User(
                                            id = firebaseUser.uid,
                                            firstName = "",
                                            lastName = "",
                                            username = "",
                                            email = email,
                                            phone = "",
                                            isActivated = false,
                                            walletBalance = 0.0,
                                            totalWithdrawn = 0.0,
                                            totalEarnings = 0.0,
                                            completedTasks = mutableListOf(),
                                            withdrawals = mutableListOf(),
                                            transactions = mutableListOf(),
                                            referralCode = ReferralCodeGenerator.generateCode("user"),
                                            referredBy = "",
                                            referrals = mutableListOf(),
                                            referralEarnings = 0.0
                                        )
                                        continuation.resume(Result.success(defaultUser))
                                    }
                                } else {
                                    // Create new user document if it doesn't exist
                                    val newUser = User(
                                        id = firebaseUser.uid,
                                        firstName = "",
                                        lastName = "",
                                        username = "",
                                        email = email,
                                        phone = "",
                                        isActivated = false,
                                        walletBalance = 0.0,
                                        totalWithdrawn = 0.0,
                                        totalEarnings = 0.0,
                                        completedTasks = mutableListOf(),
                                        withdrawals = mutableListOf(),
                                        transactions = mutableListOf(),
                                        referralCode = ReferralCodeGenerator.generateCode("user"),
                                        referredBy = "",
                                        referrals = mutableListOf(),
                                        referralEarnings = 0.0
                                    )

                                    firestore.collection("users")
                                        .document(newUser.id)
                                        .set(newUser)

                                    continuation.resume(Result.success(newUser))
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error loading user from Firestore", e)
                                val basicUser = User(
                                    id = firebaseUser.uid,
                                    firstName = "",
                                    lastName = "",
                                    username = "",
                                    email = email,
                                    phone = "",
                                    isActivated = false,
                                    walletBalance = 0.0,
                                    totalWithdrawn = 0.0,
                                    totalEarnings = 0.0,
                                    completedTasks = mutableListOf(),
                                    withdrawals = mutableListOf(),
                                    transactions = mutableListOf(),
                                    referralCode = ReferralCodeGenerator.generateCode("user"),
                                    referredBy = "",
                                    referrals = mutableListOf(),
                                    referralEarnings = 0.0
                                )
                                continuation.resume(Result.success(basicUser))
                            }
                    } else {
                        continuation.resume(Result.failure(Exception("User not found")))
                    }
                } else {
                    val errorMsg = task.exception?.message ?: "Login failed"
                    Log.e(TAG, "Login failed: $errorMsg")
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
            }
    }

    // Find user by username
    suspend fun findUserByUsername(username: String): User? = suspendCoroutine { continuation ->
        firestore.collection("users")
            .whereEqualTo("username", username.lowercase())
            .get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty()) {
                    val user = documents.first().toObject(User::class.java)
                    continuation.resume(user)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    // Update user in Firestore
    suspend fun updateUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->
        firestore.collection("users")
            .document(user.id)
            .set(user)
            .addOnSuccessListener {
                Log.d(TAG, "User updated in Firestore: ${user.id}")
                continuation.resume(Result.success(true))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error updating user in Firestore", e)
                continuation.resume(Result.failure(e))
            }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }
}
