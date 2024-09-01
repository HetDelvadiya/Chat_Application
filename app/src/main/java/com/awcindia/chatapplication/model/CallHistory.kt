package com.awcindia.chatapplication.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "call_history")
data class CallHistory(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val callerId: String,
    val receiverId: String,
    val receiverName : String ,
    val callDate : Long ,
    val callType: String, // e.g., "voice" or "video"
    val timestamp: Long
) {
}