package com.awcindia.chatapplication.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.CallListBinding
import com.awcindia.chatapplication.model.CallHistory
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CallHistoryAdapter(
    private val context: Context,
) : ListAdapter<CallHistory, CallHistoryAdapter.CallHistoryViewHolder>(CallHistoryDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CallHistoryViewHolder {
        val binding = CallListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CallHistoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CallHistoryViewHolder, position: Int) {
        val callHistory = getItem(position)
        holder.bind(callHistory)
    }

    class CallHistoryViewHolder(private val binding: CallListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SimpleDateFormat")
        fun bind(callHistory: CallHistory) {
            // Bind receiver's name
            binding.contactName.text = callHistory.receiverName

            // Format call date and set it
            binding.callDate.text = formatTimestampToDateString(callHistory.callDate)

            // Set call type image (e.g., voice or video) based on callType
            binding.callType.setImageResource(getCallTypeDrawableId(callHistory.callType))
        }

        private fun formatTimestampToDateString(timestamp: Long): String {
            val date = Date(timestamp)
            val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
            return dateFormat.format(date)
        }

        private fun getCallTypeDrawableId(callType: String): Int {
            return when (callType) {
                "voice" -> R.drawable.call // Ensure this is correct
                "video" -> R.drawable.videocam // Ensure this is correct
                else -> R.drawable.ic_call_end // Default icon
            }
        }
    }
    class CallHistoryDiffCallback : DiffUtil.ItemCallback<CallHistory>() {
        override fun areItemsTheSame(oldItem: CallHistory, newItem: CallHistory): Boolean {
            // Check if items are the same using unique id
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CallHistory, newItem: CallHistory): Boolean {
            // Check if the content of items is the same
            return oldItem == newItem
        }
    }
}
