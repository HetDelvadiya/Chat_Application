package com.awcindia.chatapplication.ViewModelFactory

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.repository.ChatListRepository
import com.awcindia.chatapplication.ui.adapter.ChatListAdapter
import com.awcindia.chatapplication.ui.viewmodel.ChatListViewModel

@Suppress("UNCHECKED_CAST")
class ChatListFactory(val repository: ChatListRepository) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return ChatListViewModel(repository) as T
    }

}