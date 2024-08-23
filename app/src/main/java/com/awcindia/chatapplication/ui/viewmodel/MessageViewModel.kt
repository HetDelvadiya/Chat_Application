package com.awcindia.chatapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.model.TypingStatus
import com.awcindia.chatapplication.repository.MassageRepository

class MessageViewModel(private val repository: MassageRepository) : ViewModel() {

    fun getMessages(chatId: String): LiveData<List<MessageData>> {
        return repository.getMessages(chatId)
    }

    fun sendMessage(chatId: String, message: MessageData) {
        repository.sendMessage(chatId, message)
    }

//    fun setTypingStatus(chatId: String, userId: String, isTyping: Boolean) {
//        repository.setTypingStatus(chatId, userId, isTyping)
//    }
//
//    fun getTypingStatus(chatId: String): LiveData<Map<String, Boolean>> {
//        return repository.getTypingStatus(chatId)
//    }
}
