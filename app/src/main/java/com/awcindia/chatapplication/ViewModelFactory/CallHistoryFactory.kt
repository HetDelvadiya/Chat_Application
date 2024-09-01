package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.CallHistoryRepository
import com.awcindia.chatapplication.viewmodel.CallHistoryViewModel

class CallHistoryFactory(val repository: CallHistoryRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallHistoryViewModel(repository) as T
    }
}