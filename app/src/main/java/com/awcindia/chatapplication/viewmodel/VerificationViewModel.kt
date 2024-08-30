package com.awcindia.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.repository.AuthStates
import com.awcindia.chatapplication.repository.VerificationRepository
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch

class VerificationViewModel(private val repository: VerificationRepository) : ViewModel() {


    private val _authState = MutableLiveData<AuthStates>()
    val authState: LiveData<AuthStates> get() = _authState

    fun verifyCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        viewModelScope.launch {
            _authState.value = AuthStates.Loading
            val result = repository.signInWithPhoneAuthCredential(credential)
            if (result.isSuccess) {
                _authState.value = AuthStates.Authenticated(result.getOrNull())
            } else {
                _authState.value =
                    AuthStates.Error(result.exceptionOrNull()?.message ?: "Unknown error")
            }
        }
    }
}