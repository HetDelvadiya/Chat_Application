package com.awcindia.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.model.CallHistory
import com.awcindia.chatapplication.repository.CallHistoryRepository
import kotlinx.coroutines.launch

class CallHistoryViewModel(val repository: CallHistoryRepository) : ViewModel() {

    fun getCallHistory(): LiveData<List<CallHistory>> {
        return repository.getCallHistory()
    }

    fun insertCallHistory(callHistory: CallHistory) {
        viewModelScope.launch {
            repository.insertCallHistory(callHistory)
        }
    }
}