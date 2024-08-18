package com.awcindia.chatapplication.repository

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awcindia.chatapplication.model.MessageData
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class MassageRepository(val firestore: FirebaseFirestore) {
    fun receiveMessage(senderRoom: String): LiveData<List<MessageData>> {
        val messagesLiveData = MutableLiveData<List<MessageData>>()

        firestore.collection("messages")
            .document(senderRoom)
            .collection("chats")
            .orderBy("timestamp")
            .addSnapshotListener { snapshot, e ->
                if (e != null) {
                    Log.w("ChatRepository", "Listen failed.", e)
                    return@addSnapshotListener
                }

                if (snapshot != null && !snapshot.isEmpty) {
                    val messagesList = snapshot.toObjects(MessageData::class.java)
                    messagesLiveData.postValue(messagesList)
                }
            }

        return messagesLiveData
    }

    suspend fun sendMessage(senderRoom: String, receiverRoom: String, messageData: MessageData) {

        try {
            // Add message to sender's room
            firestore.collection("messages").document(senderRoom)
                .collection("chats").add(messageData).await()

            // Add message to receiver's room
            firestore.collection("messages").document(receiverRoom)
                .collection("chats").add(messageData).await()

        } catch (e: Exception) {
            // Handle the exception
            Log.e("sendMessage", "Error sending message: ${e.message}")
        }
    }
}