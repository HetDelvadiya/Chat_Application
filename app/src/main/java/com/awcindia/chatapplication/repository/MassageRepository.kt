package com.awcindia.chatapplication.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.model.TypingStatus
import com.google.firebase.firestore.FirebaseFirestore

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

    fun getMessages(chatId: String): LiveData<List<MessageData>> {
        val messagesLiveData = MutableLiveData<List<MessageData>>()

        firestore.collection("chats")
            .document(chatId)
            .collection("messages")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, _ ->
                val messages = snapshot?.documents?.mapNotNull { document ->
                    document.toObject(MessageData::class.java)
                }
                messagesLiveData.value = messages!!
            }

        return messagesLiveData
    }

    fun updateTypingStatus(currentUserId: String, isTyping: Boolean) {
        firestore.collection("users").document(currentUserId).update("typing", isTyping)
            .addOnSuccessListener {
                Log.d("UserTyping", "Typing status updated to $isTyping")
            }
            .addOnFailureListener { e ->
                Log.e("UserTyping", "Error updating typing status: ${e.message}")
            }
    }

    fun getUserTypingStatus(receiverId: String): LiveData<Boolean> {
        val typingLiveData = MutableLiveData<Boolean>()

        firestore.collection("users").document(receiverId)
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("UserTyping", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && snapshot.exists()) {
                    val isTyping = snapshot.getBoolean("typing") ?: false
                    typingLiveData.postValue(isTyping)
                } else {
                    typingLiveData.postValue(false)
                }
            }
        return typingLiveData
    }
}

