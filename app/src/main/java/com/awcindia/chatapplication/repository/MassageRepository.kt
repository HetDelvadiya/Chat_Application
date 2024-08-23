package com.awcindia.chatapplication.repository

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

    fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
        val typingStatusRef = firestore.collection("chats")
            .document(chatId)
            .collection("typingStatus")
            .document(userId)

        val status = mapOf(
            "isTyping" to isTyping,
            "timestamp" to System.currentTimeMillis()
        )

        typingStatusRef.set(status)
    }

    fun getTypingStatus(chatId: String): LiveData<Map<String, Boolean>> {
        val liveData = MutableLiveData<Map<String, Boolean>>()
        firestore.collection("chats")
            .document(chatId)
            .collection("typingStatus")
            .addSnapshotListener { snapshot, _ ->
                val typingStatus = snapshot?.documents?.associate { doc ->
                    doc.id to (doc.getBoolean("isTyping") ?: false)
                }
                liveData.value = typingStatus!!
            }
        return liveData
    }
}

