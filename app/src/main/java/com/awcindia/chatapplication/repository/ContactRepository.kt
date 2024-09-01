package com.awcindia.chatapplication.repository

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.utils.ContactsManager
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QueryDocumentSnapshot

class ContactRepository(private val firestore: FirebaseFirestore, private val context: Context) {

    private val contactLiveData = MutableLiveData<List<User>>()
    fun getAllUser(): LiveData<List<User>> {
        val deviceContacts = ContactsManager(context).getContacts()
        val matchedContacts = mutableListOf<User>()

        if (deviceContacts.isNotEmpty()) {
            for (contact in deviceContacts) {
                val query = firestore.collection("users")
                    .whereEqualTo("userPhoneNumber", contact.phoneNumber)
                query.get().addOnCompleteListener { task ->
                    if (task.isSuccessful && !task.result.isEmpty) {
                        for (docs in task.result) {
                            val user = getUserFromDocument(docs, contact.name)
                            matchedContacts.add(user)
                        }
                        // Update LiveData only after adding all users from this query
                        contactLiveData.postValue(matchedContacts)
                    }
                }
            }
        } else {
            Log.d("getAllUser", "No device contacts available")
        }

        return contactLiveData
    }

    private fun getUserFromDocument(docs: QueryDocumentSnapshot, contactName: String): User {
        return User(
            userName = contactName,
            userPhoneNumber = docs.getString("userPhoneNumber") ?: "",
            userImage = docs.getString("userImage") ?: "",
            userId = docs.getString("userId") ?: ""
        )
    }
}