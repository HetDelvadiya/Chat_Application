package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.MassageRepository
import com.awcindia.chatapplication.ui.viewmodel.ReceiverViewModel
import com.awcindia.chatapplication.ui.viewmodel.SenderViewModel

class MessageFactory(val repository: MassageRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(SenderViewModel::class.java) -> SenderViewModel(repository) as T
            modelClass.isAssignableFrom(ReceiverViewModel::class.java) -> ReceiverViewModel(
                repository
            ) as T

            else -> {
                throw IllegalArgumentException("Unknown viewModel class")
            }
        }
    }
}