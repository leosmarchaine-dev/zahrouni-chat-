package com.example.data.remote

import android.content.Context
import android.util.Log
import com.example.data.model.Message
import com.example.data.model.MessageType
import com.example.data.model.Story
import com.example.data.model.User
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseService(private val context: Context) {

    private val TAG = "FirebaseService"

    val isFirebaseInitialized: Boolean
        get() = try {
            FirebaseApp.getApps(context).isNotEmpty()
        } catch (e: Exception) {
            false
        }

    private val db: FirebaseFirestore?
        get() = if (isFirebaseInitialized) try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Firestore not initialized: ${e.message}")
            null
        } else null

    private val auth: FirebaseAuth?
        get() = if (isFirebaseInitialized) try {
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "FirebaseAuth not initialized: ${e.message}")
            null
        } else null

    fun getFirebaseStatus(): String {
        return if (isFirebaseInitialized) {
            val user = auth?.currentUser
            if (user != null) "Connected as ${user.email ?: user.uid} (zahrouni-chat 🔥)"
            else "Firebase Connected Live: project zahrouni-chat 🔥"
        } else {
            "Firebase SDK Ready — Add google-services.json to /app directory to enable cloud syncing."
        }
    }

    suspend fun saveUserToFirestore(user: User): Boolean {
        val firestore = db ?: return false
        return try {
            val userMap = hashMapOf(
                "id" to user.id,
                "name" to user.name,
                "username" to user.username,
                "email" to user.email,
                "phoneNumber" to user.phoneNumber,
                "avatarUrl" to user.avatarUrl,
                "bio" to user.bio,
                "city" to user.city,
                "lastActive" to System.currentTimeMillis()
            )
            firestore.collection("users").document(user.id).set(userMap).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to save user to Firestore: ${e.message}")
            false
        }
    }

    suspend fun publishStoryToFirestore(story: Story): Boolean {
        val firestore = db ?: return false
        return try {
            val storyMap = hashMapOf(
                "id" to story.id,
                "userId" to story.userId,
                "userName" to story.userName,
                "userAvatar" to story.userAvatar,
                "mediaUrl" to story.mediaUrl,
                "textContent" to story.textContent,
                "timestamp" to story.timestamp,
                "createdAt" to System.currentTimeMillis()
            )
            firestore.collection("stories").document(story.id).set(storyMap).await()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to publish story to Firestore: ${e.message}")
            false
        }
    }

    fun observeCloudMessages(chatId: String): Flow<List<Message>> = callbackFlow {
        val firestore = db
        if (firestore == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = firestore.collection("chats").document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e(TAG, "Listen failed: ${error.message}")
                    return@addSnapshotListener
                }
                if (snapshot != null) {
                    val messages = snapshot.documents.mapNotNull { doc ->
                        try {
                            val typeStr = doc.getString("type") ?: "TEXT"
                            val typeEnum = try { MessageType.valueOf(typeStr) } catch (e: Exception) { MessageType.TEXT }
                            Message(
                                id = doc.getString("id") ?: doc.id,
                                chatId = doc.getString("chatId") ?: chatId,
                                senderId = doc.getString("senderId") ?: "",
                                senderName = doc.getString("senderName") ?: "",
                                senderAvatar = doc.getString("senderAvatar") ?: "",
                                content = doc.getString("content") ?: "",
                                type = typeEnum,
                                mediaUrl = doc.getString("mediaUrl") ?: "",
                                timestamp = doc.getLong("timestamp") ?: System.currentTimeMillis()
                            )
                        } catch (e: Exception) {
                            null
                        }
                    }
                    trySend(messages)
                }
            }

        awaitClose { listener.remove() }
    }
}
