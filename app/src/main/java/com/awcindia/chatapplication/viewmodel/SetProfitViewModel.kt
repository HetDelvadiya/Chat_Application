package com.awcindia.chatapplication.viewmodel

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.model.UserPhoneNumber
import com.awcindia.chatapplication.repository.SetProfileRepository
import com.google.firebase.auth.FirebaseAuth
import java.util.UUID

class SetProfitViewModel(
    private val repository: SetProfileRepository,
    private val auth: FirebaseAuth,
) : ViewModel() {

    private val _uploadState = MutableLiveData<UploadState>()
    val uploadState: LiveData<UploadState> = _uploadState

    fun saveUserProfile(userName: String, userImage: Uri?) {
        val userID = auth.currentUser?.uid
        if (userID == null || userName.isEmpty() || userImage == null) {
            _uploadState.value = UploadState.Error("Please enter user name and select an image")
            return
        }

        val imageRef = repository.getImageReference(UUID.randomUUID().toString())
        _uploadState.value = UploadState.Loading

        repository.uploadImage(imageRef, userImage).addOnSuccessListener { downloadUri ->
            val user = User(userID, userName, downloadUri.toString() , UserPhoneNumber.phoneNumber)
            repository.saveUserProfile(user).addOnCompleteListener {
                _uploadState.value = UploadState.Success
            }.addOnFailureListener { e ->
                _uploadState.value = UploadState.Error("Failed to set profile: ${e.message}")
            }
        }.addOnFailureListener { e ->
            _uploadState.value = UploadState.Error("Failed to upload image: ${e.message}")
        }
    }
}

sealed class UploadState {
    object Loading : UploadState()
    object Success : UploadState()
    data class Error(val message: String) : UploadState()
}