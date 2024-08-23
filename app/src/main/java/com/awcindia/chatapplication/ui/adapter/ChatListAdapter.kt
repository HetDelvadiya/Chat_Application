package com.awcindia.chatapplication.ui.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ChatListBinding
import com.awcindia.chatapplication.model.User
import com.awcindia.chatapplication.ui.activity.MessageChatActivity
import com.bumptech.glide.Glide


class ChatListAdapter : ListAdapter<User, ChatListAdapter.ChatListViewHolder>(UserDiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatListViewHolder {
        val binding = ChatListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ChatListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ChatListViewHolder, position: Int) {
        val chatItem = getItem(position)
        holder.bind(chatItem)
    }

    inner class ChatListViewHolder(private val binding: ChatListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(chatItem: User) {
            Glide.with(itemView.context)
                .load(chatItem.userImage)
                .placeholder(R.drawable.default_dp)
                .error(R.drawable.default_dp)
                .into(binding.profileImage)

            binding.contactName.text = chatItem.userName

            binding.openChat.setOnClickListener {
                val context = itemView.context
                val intent = Intent(context, MessageChatActivity::class.java).apply {
                    putExtra("userId", chatItem.userId)
                    putExtra("userName", chatItem.userName)
                    putExtra("userImage", chatItem.userImage)
                }
                context.startActivity(intent)
            }
        }
    }
}

object UserDiffCallback : DiffUtil.ItemCallback<User>() {
    override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
        // Check if items are the same based on unique ID
        return oldItem.userId == newItem.userId
    }

    override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
        // Check if the contents are the same
        return oldItem == newItem
    }
}