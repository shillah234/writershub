package com.writershub.app.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.User
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseAuthManager {
    private val auth = Firebase.auth

    // Sign up with email and password - SIMPLIFIED VERSION
    suspend fun signUp(
        email: String,
        password: String,
        name: String,
        phone: String
    ): Result<User> = suspendCoroutine { continuation ->
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    // Create our user model
                    val newUser = User(
                        id = firebaseUser?.uid ?: "",
                        name = name,
                        email = email,
                        phone = phone,
                        isActivated = false,
                        walletBalance = 0.0,
                        totalWithdrawn = 0.0,
                        totalEarnings = 0.0
                    )

                    // Return success without Firestore for now
                    continuation.resume(Result.success(newUser))

                } else {
                    val errorMsg = task.exception?.message ?: "Registration failed"
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
            }
    }

    // Login with email and password
    suspend fun login(email: String, password: String): Result<User> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    // Create basic user object
                    val user = User(
                        id = firebaseUser?.uid ?: "",
                        name = firebaseUser?.displayName ?: "",
                        email = email,
                        phone = "",
                        isActivated = false,
                        walletBalance = 0.0,
                        totalWithdrawn = 0.0,
                        totalEarnings = 0.0
                    )

                    continuation.resume(Result.success(user))

                } else {
                    val errorMsg = task.exception?.message ?: "Login failed"
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
            }
    }

    // Logout
    fun logout() {
        auth.signOut()
    }

    // Update user (placeholder for now)
    suspend fun updateUser(user: User): Result<Boolean> = suspendCoroutine { continuation ->
        continuation.resume(Result.success(true))
    }
}