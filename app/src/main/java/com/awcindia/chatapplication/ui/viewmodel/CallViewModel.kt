package com.awcindia.chatapplication.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.awcindia.chatapplication.repository.CallRepository
import com.zegocloud.uikit.prebuilt.call.ZegoUIKitPrebuiltCallService
import com.zegocloud.uikit.prebuilt.call.invite.ZegoUIKitPrebuiltCallInvitationConfig
import kotlinx.coroutines.launch

class CallViewModel(private val application: Application) : ViewModel() {


    private val callRepository = CallRepository()
    private val appId: Long = 660355364L
    private val appSign: String = "b0e5ffedb80419c43a6faf69bc28456e8c95e7ee94d0dccab7c2e2cdd0b57159"
    private val _receiverPhoneNumber = MutableLiveData<String>()
    val receiverPhoneNumber: LiveData<String> get() = _receiverPhoneNumber
    fun setUpZeGoUIKit(userName : String) {

        viewModelScope.launch {
            val phoneNumber = callRepository.getCurrentUserPhoneNumber()
            if (phoneNumber != null) {
                val callInvitationConfig = ZegoUIKitPrebuiltCallInvitationConfig()
                ZegoUIKitPrebuiltCallService.init(
                    application,
                    appId,
                    appSign,
                    phoneNumber,  // Use phone number as userId
                    userName,  // Use phone number as userName
                    callInvitationConfig
                )
            } else {
                // Handle the case where phone number is not available
                Log.e("CallViewModel", "No phone number found for the current user.")
            }
        }
    }
    fun getReceiverPhoneNumber(receiverId: String) {
        viewModelScope.launch {
            val phoneNumber = callRepository.getReceiverPhoneNumber(receiverId)
            if (phoneNumber != null) {
                _receiverPhoneNumber.value = "+91$phoneNumber"
                Log.d("PhoneNumber", "+91$phoneNumber")
            } else {
                Log.e("VoiceCallViewModel", "Failed to fetch receiver's phone number.")
            }
        }
    }


    fun unInitZeGoUIKit() {
        ZegoUIKitPrebuiltCallService.unInit()
    }
}


