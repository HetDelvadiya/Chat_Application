package com.awcindia.chatapplication.ui.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.model.Contact
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.repository.ContactRepository

class ContactViewModel(val repository: ContactRepository) : ViewModel() {
    fun getAllUser(deviceContacts: List<Contact>): LiveData<List<User>> {
        return repository.getAllUser(deviceContacts)
    }
}
