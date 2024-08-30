package com.awcindia.chatapplication.viewmodel


import android.app.Activity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.repository.AuthRepository
import com.google.firebase.FirebaseException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch


class AuthViewModel(val repository: AuthRepository) : ViewModel() {

    private val _verificationId = MutableLiveData<String>()

    val verificationId: LiveData<String> get() = _verificationId

    private val _authState = MutableLiveData<AuthState>()
    val authState: LiveData<AuthState> get() = _authState

    fun sendVerificationCode(phoneNumber: String, activity: Activity) {

        viewModelScope.launch {

            try {

                _authState.value = AuthState.Loading

                repository.sendVerificationCode(
                    phoneNumber,
                    object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                        override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                            launch {
                                repository.signInWithPhoneAuthCredential(p0)
                                _authState.value = AuthState.Success
                            }
                        }

                        override fun onVerificationFailed(p0: FirebaseException) {
                            _authState.value = AuthState.Failure(p0.message.toString())
                        }

                        override fun onCodeSent(
                            p0: String,
                            p1: PhoneAuthProvider.ForceResendingToken,
                        ) {
                            super.onCodeSent(p0, p1)
                            _verificationId.value = p0
                            _authState.value = AuthState.CodeSent
                        }

                    },
                    activity)
            } catch (e: Exception) {
                _authState.value = AuthState.Failure(e.message.toString())
            }
        }
    }
}

sealed class AuthState {

    data object Loading : AuthState()
    data object Success : AuthState()
    data object CodeSent : AuthState()

    data class Failure(val error: String) : AuthState()
}