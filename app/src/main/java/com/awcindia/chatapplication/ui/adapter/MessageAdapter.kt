package com.awcindia.chatapplication.ui.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.awcindia.chatapplication.databinding.ItemMessageReceivedBinding
import com.awcindia.chatapplication.databinding.ItemMessageSentBinding
import com.awcindia.chatapplication.model.MessageData
import com.bumptech.glide.Glide
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MessageAdapter(
    private val context: Context,
    private val currentUserId: String,
) : ListAdapter<MessageData, MessageAdapter.MessageViewHolder>(MessageDiffCallback()) {

    private val VIEW_TYPE_SENT = 1
    private val VIEW_TYPE_RECEIVED = 2

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val binding = if (viewType == VIEW_TYPE_SENT) {
            ItemMessageSentBinding.inflate(LayoutInflater.from(context), parent, false)
        } else {
            ItemMessageReceivedBinding.inflate(LayoutInflater.from(context), parent, false)
        }
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = getItem(position) // Use getItem to retrieve the current item
        holder.bind(message)
    }

    override fun getItemViewType(position: Int): Int {
        return if (getItem(position).senderId == currentUserId) {
            VIEW_TYPE_SENT
        } else {
            VIEW_TYPE_RECEIVED
        }
    }

    inner class MessageViewHolder(val binding: ViewBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(message: MessageData) {
            when (binding) {
                is ItemMessageSentBinding -> {
                    binding.messageText.text = message.message
                    binding.messageImage.visibility =
                        if (message.messageType == "text") View.GONE else View.VISIBLE
                    if (message.messageType != "text") {
                        Glide.with(context)
                            .load(message.imageUrl)
                            .into(binding.messageImage)
                    }

                    binding.seenIndicatore.text = if (message.seenByReceiver) "seen" else "sent"
                }

                is ItemMessageReceivedBinding -> {
                    binding.messageText.text = message.message
                    binding.messageImage.visibility =
                        if (message.messageType == "text") View.GONE else View.VISIBLE
                    if (message.messageType != "text") {
                        Glide.with(context)
                            .load(message.imageUrl)
                            .into(binding.messageImage)
                    }
                }
            }

            // Format and display the timestamp
            val timestamp = message.timestamp
            val formattedTime =
                SimpleDateFormat("hh:mm a", Locale.getDefault()).format(Date(timestamp))
            when (binding) {
                is ItemMessageSentBinding -> binding.messageTimestamp.text = formattedTime
                is ItemMessageReceivedBinding -> binding.messageTimestamp.text = formattedTime
            }
        }
    }
    class MessageDiffCallback : DiffUtil.ItemCallback<MessageData>() {

        override fun areItemsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
            return oldItem.messageId == newItem.messageId
        }

        override fun areContentsTheSame(oldItem: MessageData, newItem: MessageData): Boolean {
            return oldItem == newItem
        }
    }
}
