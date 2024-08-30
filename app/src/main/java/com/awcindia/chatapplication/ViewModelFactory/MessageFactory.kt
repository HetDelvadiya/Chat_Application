package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.MassageRepository
import com.awcindia.chatapplication.viewmodel.MessageViewModel

class MessageFactory(val repository: MassageRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return MessageViewModel(repository) as T
    }
}