package com.awcindia.chatapplication.repository

import android.app.Activity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import java.util.concurrent.TimeUnit
import kotlin.coroutines.coroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class AuthRepository(private val auth: FirebaseAuth) {

     fun sendVerificationCode(
        phoneNumber: String,
        callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks,
        activity: Activity
     ) {

         val options = PhoneAuthOptions.newBuilder(auth)
             .setPhoneNumber(phoneNumber)
             .setTimeout(60L, TimeUnit.SECONDS)
             .setActivity(activity)  // Make sure you use requireActivity() if called from a Fragment
             .setCallbacks(callbacks)
             .build()
         PhoneAuthProvider.verifyPhoneNumber(options)
    }

    suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        suspendCancellableCoroutine<Unit> { continuation ->

            auth.signInWithCredential(credential)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        continuation.resume(Unit)
                    } else {
                        continuation.resumeWithException(
                            task.exception ?: Exception("Unknown error")
                        )
                    }
                }
        }
    }
}