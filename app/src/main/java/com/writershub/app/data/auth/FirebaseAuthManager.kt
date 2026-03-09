package com.writershub.app.data.auth

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.User
import com.writershub.app.data.model.Withdrawal
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object FirebaseAuthManager {
    private val auth = Firebase.auth
    private val firestore = Firebase.firestore
    private val TAG = "FirebaseAuthManager"

    // Sign up with email and password - WITH FIRESTORE
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
                        totalEarnings = 0.0,
                        completedTasks = mutableListOf(),
                        withdrawals = mutableListOf()
                    )

                    // Save to Firestore
                    firestore.collection("users")
                        .document(newUser.id)
                        .set(newUser)
                        .addOnSuccessListener {
                            Log.d(TAG, "User saved to Firestore: ${newUser.id}")
                            continuation.resume(Result.success(newUser))
                        }
                        .addOnFailureListener { e ->
                            Log.e(TAG, "Error saving user to Firestore", e)
                            // Still return success since auth worked, but log the error
                            continuation.resume(Result.success(newUser))
                        }

                } else {
                    val errorMsg = task.exception?.message ?: "Registration failed"
                    Log.e(TAG, "Registration failed: $errorMsg")
                    continuation.resume(Result.failure(Exception(errorMsg)))
                }
            }
    }

    // Login with email and password - WITH FIRESTORE
    suspend fun login(email: String, password: String): Result<User> = suspendCoroutine { continuation ->
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val firebaseUser = auth.currentUser

                    if (firebaseUser != null) {
                        // Get user data from Firestore
                        firestore.collection("users")
                            .document(firebaseUser.uid)
                            .get()
                            .addOnSuccessListener { document ->
                                if (document.exists()) {
                                    val user = document.toObject(User::class.java)
                                    if (user != null) {
                                        Log.d(TAG, "User loaded from Firestore: ${user.id}")
                                        continuation.resume(Result.success(user))
                                    } else {
                                        // Create default user if data corrupted
                                        val defaultUser = User(
                                            id = firebaseUser.uid,
                                            name = firebaseUser.displayName ?: "",
                                            email = email,
                                            phone = "",
                                            isActivated = false,
                                            walletBalance = 0.0,
                                            totalWithdrawn = 0.0,
                                            totalEarnings = 0.0
                                        )
                                        continuation.resume(Result.success(defaultUser))
                                    }
                                } else {
                                    // Create new user document if it doesn't exist
                                    val newUser = User(
                                        id = firebaseUser.uid,
                                        name = firebaseUser.displayName ?: "",
                                        email = email,
                                        phone = "",
                                        isActivated = false,
                                        walletBalance = 0.0,
                                        totalWithdrawn = 0.0,
                                        totalEarnings = 0.0
                                    )

                                    firestore.collection("users")
                                        .document(newUser.id)
                                        .set(newUser)

                                    continuation.resume(Result.success(newUser))
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.e(TAG, "Error loading user from Firestore", e)
                                // Return basic user if Firestore fails
                                val basicUser = User(
                                    id = firebaseUser.uid,
                                    name = firebaseUser.displayName ?: "",
                                    email = email,
                                    phone = "",
                                    isActivated = false,
                                    walletBalance = 0.0,
                                    totalWithdrawn = 0.0,
                                    totalEarnings = 0.0
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