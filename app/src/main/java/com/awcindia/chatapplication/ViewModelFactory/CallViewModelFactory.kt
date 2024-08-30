package com.awcindia.chatapplication.ViewModelFactory

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.awcindia.chatapplication.ui.viewmodel.CallViewModel

class CallViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return CallViewModel(application) as T
    }
}