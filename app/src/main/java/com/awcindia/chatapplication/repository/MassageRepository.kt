package com.awcindia.chatapplication.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.model.User
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class MassageRepository() {
    private val firestore = FirebaseFirestore.getInstance()

    fun sendMessage(chatId: String, message: MessageData) {
        val messageDoc = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document()

        val messageWithId = message.copy(messageId = messageDoc.id)
        messageDoc.set(messageWithId)
    }

    fun getMessages(chatId: String ): LiveData<List<MessageData>> {
        val messagesLiveData = MutableLiveData<List<MessageData>>()

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { document ->
                    val message = document.toObject(MessageData::class.java)
                    message
                }
                messagesLiveData.value = messages ?: emptyList()
            }

        return messagesLiveData
    }

    suspend fun markMessageAsSeen(chatId: String, messageId: String) {
        val messageRef = firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .document(messageId)

        messageRef.update("seenByReceiver", true).await()
    }

    suspend fun updateTypingStatus(currentUserId: String, isTyping: Boolean) {
        try {
            firestore.collection("users").document(currentUserId).update("typing", isTyping).await()
            Log.d("UserTyping", "Typing status updated to $isTyping")
        } catch (e: Exception) {
            Log.e("UserTyping", "Error updating typing status: ${e.message}")
        }
    }

    fun getUserTypingStatus(receiverId: String): Flow<Boolean> = callbackFlow {
        val listenerRegistration = firestore.collection("users").document(receiverId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("UserTyping", "Listen failed.", e)
                    trySend(false).isSuccess
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val isTyping = snapshot.getBoolean("typing") ?: false
                    trySend(isTyping).isSuccess
                } else {
                    trySend(false).isSuccess
                }
            }

        awaitClose { listenerRegistration.remove() }
    }

}

