package com.writershub.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.writershub.app.data.model.Announcement
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

object AnnouncementRepository {
    private val db = Firebase.firestore
    private val TAG = "AnnouncementRepository"

    // Get active announcements for users - SIMPLIFIED VERSION
    suspend fun getActiveAnnouncements(): List<Announcement> = suspendCoroutine { continuation ->
        Log.d(TAG, "🔍 Fetching active announcements...")

        db.collection("announcements")
            .whereEqualTo("isActive", true)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .limit(10)
            .get()
            .addOnSuccessListener { documents ->
                Log.d(TAG, "✅ Found ${documents.size()} announcements in Firestore")

                documents.forEach { doc ->
                    Log.d(TAG, "📄 Document: ${doc.data}")
                }

                val announcements = documents.map { doc ->
                    doc.toObject(Announcement::class.java).copy(id = doc.id)
                }

                if (announcements.isNotEmpty()) {
                    announcements.forEach { announcement ->
                        Log.d(TAG, "📢 Announcement: ${announcement.title} (Active: ${announcement.isActive})")
                    }
                } else {
                    Log.d(TAG, "📭 No announcements found")
                }

                continuation.resume(announcements)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error loading announcements: ${e.message}")
                e.printStackTrace()
                continuation.resume(emptyList())
            }
    }

    // Get all announcements (for admin)
    suspend fun getAllAnnouncements(): List<Announcement> = suspendCoroutine { continuation ->
        Log.d(TAG, "🔍 Fetching all announcements...")

        db.collection("announcements")
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .addOnSuccessListener { documents ->
                val announcements = documents.map { doc ->
                    doc.toObject(Announcement::class.java).copy(id = doc.id)
                }
                Log.d(TAG, "✅ Found ${announcements.size} total announcements")
                continuation.resume(announcements)
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error loading all announcements: ${e.message}")
                continuation.resume(emptyList())
            }
    }

    // Admin: Add a new announcement
    suspend fun addAnnouncement(announcement: Announcement): Result<String> = suspendCoroutine { continuation ->
        Log.d(TAG, "➕ Adding new announcement: ${announcement.title}")

        db.collection("announcements")
            .add(announcement)
            .addOnSuccessListener { docRef ->
                Log.d(TAG, "✅ Announcement added with ID: ${docRef.id}")
                continuation.resume(Result.success(docRef.id))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error adding announcement: ${e.message}")
                continuation.resume(Result.failure(e))
            }
    }

    // Admin: Update an announcement
    suspend fun updateAnnouncement(id: String, announcement: Announcement): Result<Unit> = suspendCoroutine { continuation ->
        Log.d(TAG, "✏️ Updating announcement: $id")

        db.collection("announcements")
            .document(id)
            .set(announcement)
            .addOnSuccessListener {
                Log.d(TAG, "✅ Announcement updated: $id")
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error updating announcement: ${e.message}")
                continuation.resume(Result.failure(e))
            }
    }

    // Admin: Delete an announcement
    suspend fun deleteAnnouncement(id: String): Result<Unit> = suspendCoroutine { continuation ->
        Log.d(TAG, "🗑️ Deleting announcement: $id")

        db.collection("announcements")
            .document(id)
            .delete()
            .addOnSuccessListener {
                Log.d(TAG, "✅ Announcement deleted: $id")
                continuation.resume(Result.success(Unit))
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "❌ Error deleting announcement: ${e.message}")
                continuation.resume(Result.failure(e))
            }
    }
}