package com.awcindia.chatapplication.repository

import android.net.Uri
import com.awcindia.chatapplication.model.User
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class SetProfileRepository(
    private val firestore: FirebaseFirestore,
    private val storage: FirebaseStorage,
) {

    fun getImageReference(fileName: String): StorageReference {
        return storage.reference.child("profileImage/$fileName")
    }

    fun uploadImage(imageRef: StorageReference, imageUri: Uri): Task<Uri> {
        return imageRef.putFile(imageUri).continueWithTask {
            imageRef.downloadUrl
        }
    }

    fun saveUserProfile(user: User): Task<Void> {
        return firestore.collection("users").document(user.userId).set(user)
    }
}