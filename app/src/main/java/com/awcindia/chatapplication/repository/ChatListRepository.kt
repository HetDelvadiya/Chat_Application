package com.awcindia.chatapplication.repository

import com.awcindia.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class ChatListRepository(private val firestore: FirebaseFirestore) {

    suspend fun getUserList(): List<User> {

        return try {

            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            val snapshot = firestore.collection("users").get().await()
            val allUser = snapshot.toObjects(User::class.java)

            allUser.filter { it.userId != currentUserId }


        } catch (e: Exception) {
            emptyList()
        }
    }
}