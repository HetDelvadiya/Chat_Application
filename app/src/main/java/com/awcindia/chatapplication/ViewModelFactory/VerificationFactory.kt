package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.VerificationRepository
import com.awcindia.chatapplication.ui.viewmodel.VerificationViewModel

@Suppress("UNCHECKED_CAST")
class VerificationFactory(private val repository: VerificationRepository) : ViewModelProvider.Factory{

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return VerificationViewModel(repository) as T
    }
}