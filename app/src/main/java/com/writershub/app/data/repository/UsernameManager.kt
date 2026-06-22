package com.writershub.app.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.User
import com.writershub.app.data.utils.ReferralCodeGenerator
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object UsernameManager {
    private val auth = Firebase.auth
    private val db = Firebase.firestore
    private val TAG = "UsernameManager"

    // Check if username exists (public read - works with our new rules)
    suspend fun checkUsernameExists(username: String): Boolean = suspendCoroutine { continuation ->
        val cleanUsername = username.lowercase().trim()
        db.collection("usernames").document(cleanUsername)
            .get()
            .addOnSuccessListener { document ->
                continuation.resume(document.exists())
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Error checking username: ${e.message}")
                continuation.resume(false)
            }
    }

    // Helper function to get email from username (for login)
    suspend fun getEmailFromUsername(username: String): String? = suspendCoroutine { continuation ->
        val cleanUsername = username.lowercase().trim()
        Log.d(TAG, "🔍 Looking for email from username: $cleanUsername")

        db.collection("usernames")
            .document(cleanUsername)
            .get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    val email = document.getString("email")
                    Log.d(TAG, "✅ Email found: $email")
                    continuation.resume(email)
                } else {
                    Log.d(TAG, "❌ No username found: $cleanUsername")
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                Log.e(TAG, "❌ Error getting email: ${it.message}")
                continuation.resume(null)
            }
    }

    // Helper function to get user ID from username (for login)
    suspend fun getUserIdFromUsername(username: String): String? = suspendCoroutine { continuation ->
        val cleanUsername = username.lowercase().trim()
        db.collection("usernames").document(cleanUsername)
            .get()
            .addOnSuccessListener { document ->
                val uid = document.getString("uid")
                continuation.resume(uid)
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    // Register user with username - uses atomic batch write
    suspend fun registerUser(
        firstName: String,
        lastName: String,
        username: String,
        email: String,
        phone: String,
        password: String,
        referralCode: String? = null
    ): Result<User> = suspendCoroutine { continuation ->

        val cleanUsername = username.lowercase().trim()
        Log.d(TAG, "Starting registration for username: $cleanUsername")

        // Step 1: Create Firebase Auth user
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { authTask ->
                if (!authTask.isSuccessful) {
                    val errorMsg = authTask.exception?.message ?: "Authentication failed"
                    Log.e(TAG, "Auth failed: $errorMsg")
                    continuation.resume(Result.failure(Exception(errorMsg)))
                    return@addOnCompleteListener
                }

                val uid = auth.currentUser?.uid ?: run {
                    continuation.resume(Result.failure(Exception("Failed to get UID")))
                    return@addOnCompleteListener
                }

                Log.d(TAG, "Auth successful, UID: $uid")

                // Step 2: Prepare data for batch write
                val batch = db.batch()

                // Username document - maps username -> uid AND stores email
                val usernameRef = db.collection("usernames").document(cleanUsername)
                val usernameData = hashMapOf(
                    "uid" to uid,
                    "email" to email,  // 👈 EMAIL IS NOW STORED HERE
                    "createdAt" to System.currentTimeMillis()
                )

                // User document - full profile
                val userRef = db.collection("users").document(uid)
                val referralCodeGenerated = ReferralCodeGenerator.generateReferralCode()

                val userData = hashMapOf(
                    "firstName" to firstName,
                    "lastName" to lastName,
                    "username" to cleanUsername,
                    "email" to email,
                    "phone" to phone,
                    "isActivated" to false,
                    "walletBalance" to 0.0,
                    "totalWithdrawn" to 0.0,
                    "totalEarnings" to 0.0,
                    "referralCode" to referralCodeGenerated,
                    "referredBy" to (referralCode ?: ""),
                    "completedTasks" to emptyList<String>(),
                    "withdrawals" to emptyList<Any>(),
                    "transactions" to emptyList<Any>(),
                    "referrals" to emptyList<String>(),
                    "referralEarnings" to 0.0,
                    "createdAt" to System.currentTimeMillis()
                )

                // Add both operations to batch
                batch.set(usernameRef, usernameData)
                batch.set(userRef, userData)

                // Step 3: Commit the batch
                batch.commit()
                    .addOnSuccessListener {
                        Log.d(TAG, "Batch write successful")

                        // Create User object to return
                        val newUser = User(
                            id = uid,
                            firstName = firstName,
                            lastName = lastName,
                            username = cleanUsername,
                            email = email,
                            phone = phone,
                            isActivated = false,
                            walletBalance = 0.0,
                            totalWithdrawn = 0.0,
                            totalEarnings = 0.0,
                            completedTasks = mutableListOf(),
                            withdrawals = mutableListOf(),
                            transactions = mutableListOf(),
                            referralCode = referralCodeGenerated,
                            referredBy = referralCode ?: "",
                            referrals = mutableListOf(),
                            referralEarnings = 0.0
                        )

                        continuation.resume(Result.success(newUser))
                    }
                    .addOnFailureListener { e ->
                        Log.e(TAG, "Batch write failed: ${e.message}")
                        continuation.resume(Result.failure(e))
                    }
            }
    }
}