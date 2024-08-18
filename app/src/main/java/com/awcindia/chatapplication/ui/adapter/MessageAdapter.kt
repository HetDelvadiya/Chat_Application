package com.awcindia.chatapplication.ui.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ItemMessageChatBinding
import com.awcindia.chatapplication.model.MessageData
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.Locale

class MessageAdapter(val context: Context ,private val messages: ArrayList<MessageData>) :
    RecyclerView.Adapter<MessageAdapter.MessageHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {
        val binding =
            ItemMessageChatBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageHolder(binding)
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {
        val message = messages[position]
        holder.bind(message)
    }


    inner class MessageHolder(val binding: ItemMessageChatBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(message: MessageData) {


            // Set message text and timestamp
            binding.textMessage.text = message.massage
            binding.textTimestamp.text = formatTimestamp(message.timestamp)

        }
    }

    private fun formatTimestamp(timestamp: Long): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault())
        return sdf.format(timestamp)
    }
}
