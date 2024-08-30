package com.awcindia.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository
import kotlinx.coroutines.launch

class MessageViewModel(private val repository: MassageRepository) : ViewModel() {

    fun getMessages(chatId: String): LiveData<List<MessageData>> {
        return repository.getMessages(chatId)
    }

    fun sendMessage(chatId: String, message: MessageData) {
        repository.sendMessage(chatId, message)
    }

    fun markMessageAsSeen(chatId: String, messageId: String) {
        viewModelScope.launch {
            repository.markMessageAsSeen(chatId, messageId)
        }
    }
}
