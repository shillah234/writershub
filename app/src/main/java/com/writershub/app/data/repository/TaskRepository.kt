package com.writershub.app.data.repository

import android.util.Log
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.Task
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object TaskRepository {

    private val db = Firebase.firestore
    private const val TAG = "TaskRepository"

    // ===============================
    // DAILY TASKS
    // ===============================
    suspend fun getDailyTasks(): List<Task> = suspendCoroutine { continuation ->

        Log.d(TAG, "🔍 Fetching DAILY tasks...")

        db.collection("tasks")
            .whereEqualTo("type", "DAILY")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->

                Log.d(TAG, "📄 Documents found: ${documents.size()}")

                documents.forEach {
                    Log.d(TAG, "➡️ ${it.id} -> ${it.data}")
                }

                val tasks = documents.map { document ->
                    document.toObject(Task::class.java).copy(id = document.id)
                }

                Log.d(TAG, "✅ Loaded ${tasks.size} DAILY tasks")

                continuation.resume(tasks)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to load DAILY tasks", e)
                continuation.resume(emptyList())
            }
    }

    // ===============================
    // PREMIUM TASKS
    // ===============================
    suspend fun getPremiumTasks(): List<Task> = suspendCoroutine { continuation ->

        Log.d(TAG, "🔍 Fetching PREMIUM tasks...")

        db.collection("tasks")
            .whereEqualTo("type", "PREMIUM")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->

                Log.d(TAG, "📄 Documents found: ${documents.size()}")

                documents.forEach {
                    Log.d(TAG, "➡️ ${it.id} -> ${it.data}")
                }

                val tasks = documents.map { document ->
                    document.toObject(Task::class.java).copy(id = document.id)
                }

                Log.d(TAG, "✅ Loaded ${tasks.size} PREMIUM tasks")

                continuation.resume(tasks)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to load PREMIUM tasks", e)
                continuation.resume(emptyList())
            }
    }

    // ===============================
    // VIDEO TASKS
    // ===============================
    suspend fun getVideoTasks(): List<Task> = suspendCoroutine { continuation ->

        Log.d(TAG, "🔍 Fetching VIDEO tasks...")

        db.collection("tasks")
            .whereEqualTo("type", "VIDEO")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->

                Log.d(TAG, "📄 Documents found: ${documents.size()}")

                documents.forEach {
                    Log.d(TAG, "➡️ ${it.id} -> ${it.data}")
                }

                val tasks = documents.map { document ->
                    document.toObject(Task::class.java).copy(id = document.id)
                }

                Log.d(TAG, "✅ Loaded ${tasks.size} VIDEO tasks")

                continuation.resume(tasks)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to load VIDEO tasks", e)
                continuation.resume(emptyList())
            }
    }

    // ===============================
    // GET ALL TASKS
    // ===============================
    suspend fun getAllTasks(): List<Task> = suspendCoroutine { continuation ->

        Log.d(TAG, "🔍 Fetching ALL tasks...")

        db.collection("tasks")
            .whereEqualTo("isActive", true)
            .get()
            .addOnSuccessListener { documents ->

                Log.d(TAG, "📄 Documents found: ${documents.size()}")

                documents.forEach {
                    Log.d(TAG, "➡️ ${it.id} -> ${it.data}")
                }

                val tasks = documents.map { document ->
                    document.toObject(Task::class.java).copy(id = document.id)
                }

                Log.d(TAG, "✅ Loaded ${tasks.size} tasks")

                continuation.resume(tasks)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Failed to load all tasks", e)
                continuation.resume(emptyList())
            }
    }

    // ===============================
    // GET TASK BY ID
    // ===============================
    suspend fun getTaskById(taskId: String): Task? = suspendCoroutine { continuation ->

        db.collection("tasks")
            .document(taskId)
            .get()
            .addOnSuccessListener { document ->

                if (document.exists()) {
                    val task = document.toObject(Task::class.java)?.copy(id = document.id)
                    continuation.resume(task)
                } else {
                    continuation.resume(null)
                }
            }
            .addOnFailureListener {
                continuation.resume(null)
            }
    }

    // ===============================
    // ADMIN FUNCTIONS
    // ===============================
    suspend fun addTask(task: Task): Result<String> = suspendCoroutine { continuation ->

        db.collection("tasks")
            .add(task)
            .addOnSuccessListener {
                continuation.resume(Result.success(it.id))
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }

    suspend fun updateTask(taskId: String, task: Task): Result<Unit> = suspendCoroutine { continuation ->

        db.collection("tasks")
            .document(taskId)
            .set(task)
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }

    suspend fun deleteTask(taskId: String): Result<Unit> = suspendCoroutine { continuation ->

        db.collection("tasks")
            .document(taskId)
            .delete()
            .addOnSuccessListener {
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener {
                continuation.resume(Result.failure(it))
            }
    }
}