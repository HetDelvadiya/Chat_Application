package com.awcindia.chatapplication.repository


import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class CallRepository {

    private val firestore = FirebaseFirestore.getInstance()

    suspend fun getCurrentUserPhoneNumber(): String? {
        return withContext(Dispatchers.IO) {
            val auth = FirebaseAuth.getInstance()
            val currentUser = auth.currentUser
            currentUser?.phoneNumber
        }
    }

    suspend fun getReceiverPhoneNumber(receiverId: String): String? {
        return try {
            val query = firestore.collection("users")
                .whereEqualTo("userId", receiverId)
                .get()
                .await()

            query.documents.firstOrNull()?.getString("userPhoneNumber")
        } catch (e: Exception) {
            Log.e("CallRepository", "Error fetching receiver's phone number: ${e.message}")
            null
        }
    }
}
