package com.awcindia.chatapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.repository.ChatListRepository
import kotlinx.coroutines.launch

class ChatListViewModel(val repository: ChatListRepository) : ViewModel() {

    private val _userList = MutableLiveData<List<User>>()
    val userList: LiveData<List<User>> get() = _userList
    fun getUserList(){
        viewModelScope.launch {
            val result = repository.getUserList()
            _userList.value = result

        }
    }

}