package com.awcindia.chatapplication.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.PhoneAuthCredential
import kotlinx.coroutines.tasks.await

class VerificationRepository(private val auth: FirebaseAuth) {

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential): Result<FirebaseUser?> {
        return try {
            val authResult = auth.signInWithCredential(credential).await()
            Result.success(authResult.user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

sealed class AuthStates {
    data object Loading : AuthStates()
    data class Authenticated(val user: FirebaseUser?) : AuthStates()
    data class Error(val message: String?) : AuthStates()
}