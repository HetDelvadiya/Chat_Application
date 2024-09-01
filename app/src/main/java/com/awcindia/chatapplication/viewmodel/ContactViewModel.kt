package com.awcindia.chatapplication.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.repository.ContactRepository
import kotlinx.coroutines.launch

class ContactViewModel(val repository: ContactRepository) : ViewModel() {
    fun getAllUser(): LiveData<List<User>> {
        return repository.getAllUser()
    }
}
