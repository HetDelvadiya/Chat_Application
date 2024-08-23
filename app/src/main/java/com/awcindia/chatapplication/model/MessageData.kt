package com.awcindia.chatapplication.model

import com.google.firebase.Timestamp

data class MessageData(
    val messageId: String = "",
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0L,
    val messageType: String = "",
    val imageUrl: String? = null,
    val seenByReceiver: Boolean = false
) {
}