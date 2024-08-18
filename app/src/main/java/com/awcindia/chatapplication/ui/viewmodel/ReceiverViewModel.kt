package com.awcindia.chatapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository

class ReceiverViewModel(val repository: MassageRepository) : ViewModel() {

    private val _messages = MutableLiveData<List<MessageData>>()
    val messages: LiveData<List<MessageData>> get() = _messages

    fun receiveMassage(senderRoom: String) {
        repository.receiveMessage(senderRoom).observeForever { messages ->
            _messages.postValue(messages)
        }
    }
}
