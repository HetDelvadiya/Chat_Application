package com.awcindia.chatapplication.ui.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository
import kotlinx.coroutines.launch

class SenderViewModel(val repository: MassageRepository) : ViewModel() {
    fun sendMessage(senderRoom: String, receiverRoom : String, messageData: MessageData) {

        viewModelScope.launch {
            try {
                repository.sendMessage( senderRoom ,receiverRoom, messageData)
            } catch (e: Exception) {
                Log.e("ReceiverViewModel", "Error sending message", e)
                // Handle the error appropriately if needed
            }
        }
    }
}