package com.awcindia.chatapplication.model

import com.google.firebase.Timestamp

data class MessageData(
    val massageId  : String = "" ,
    val massage  : String = "" ,
    val senderId  : String = "" ,
    val timestamp: Long   = 0 ,
    val userStatus : String = ""
) {
}