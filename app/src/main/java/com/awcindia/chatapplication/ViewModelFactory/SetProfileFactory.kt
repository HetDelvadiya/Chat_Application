package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.SetProfileRepository
import com.awcindia.chatapplication.viewmodel.SetProfitViewModel
import com.google.firebase.auth.FirebaseAuth

class SetProfileFactory(val repository: SetProfileRepository , val auth: FirebaseAuth) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return SetProfitViewModel(repository , auth) as T
    }
}