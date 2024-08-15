package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.AuthRepository
import com.awcindia.chatapplication.ui.viewmodel.AuthViewMode

class AuthViewModelFactory(val repository: AuthRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(AuthViewMode::class.java)) {
            return AuthViewMode(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}