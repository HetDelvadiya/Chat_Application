package com.awcindia.chatapplication.model

data class MessageData(
    val messageId: String = "",
    val message: String = "",
    val senderId: String = "",
    val receiverId: String = "",
    val timestamp: Long = 0L,
    val messageType: String = "",
    val imageUrl: String = "",
    val seenByReceiver: Boolean = false,
) {
}