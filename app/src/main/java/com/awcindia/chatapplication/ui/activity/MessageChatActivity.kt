package com.awcindia.chatapplication.ui.activity


import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.ViewModelFactory.MessageFactory
import com.awcindia.chatapplication.databinding.ActivityPersonsChatBinding
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository
import com.awcindia.chatapplication.ui.adapter.MessageAdapter
import com.awcindia.chatapplication.ui.viewmodel.MessageViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonsChatBinding
    private lateinit var messageViewModel: MessageViewModel

    private lateinit var chatId: String
    private lateinit var messageAdapter: MessageAdapter
    private val repository = MassageRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var receiverId: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()

        binding.progressBar.visibility = View.INVISIBLE
        messageViewModel =
            ViewModelProvider(this, MessageFactory(repository))[MessageViewModel::class.java]


        receiverId = intent.getStringExtra("userId").orEmpty()
        val userName = intent.getStringExtra("userName")
        val userImage = intent.getStringExtra("userImage")

        chatId = generateChatId(currentUserId, receiverId)

        binding.contactName.text = userName
        Glide.with(this)
            .load(userImage)
            .placeholder(R.drawable.default_dp)
            .error(R.drawable.default_dp)
            .into(binding.profileImage)

        messageAdapter = MessageAdapter(this, currentUserId)
        binding.recyclerGchat.adapter = messageAdapter
        binding.recyclerGchat.layoutManager = LinearLayoutManager(this)



        messageViewModel.getMessages(chatId).observe(this) { messages ->
            messages?.let {
                messageAdapter.submitList(it)
                binding.recyclerGchat.scrollToPosition(it.size - 1)
                // Mark received messages as seen
                markMessagesAsSeen(it)
            }
        }
        // Collect user typing status using coroutine
        lifecycleScope.launch {
            repository.getUserTypingStatus(receiverId)
                .collectLatest { isTyping ->
                    // Update status indicator based on typing status
                    val currentStatus = if (isTyping) "Typing..." else ""
                    binding.typingIndicator.text = currentStatus
                }
        }

        // Set typing status on text change
        binding.editGchatMessage.addTextChangedListener {
            lifecycleScope.launch {
                repository.updateTypingStatus(currentUserId, it.toString().isNotEmpty())
            }
        }

        binding.sendText.setOnClickListener {
            val messageText = binding.editGchatMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = MessageData(
                    message = messageText,
                    senderId = currentUserId,
                    receiverId = receiverId,
                    timestamp = System.currentTimeMillis(),
                    messageType = "text",
                    imageUrl = null,
                    seenByReceiver = false
                )
                messageViewModel.sendMessage(chatId, message)
                binding.recyclerGchat.post {
                    binding.recyclerGchat.scrollToPosition(messageAdapter.itemCount - 1)
                }
                binding.editGchatMessage.text?.clear()
                binding.recyclerGchat.post {
                    binding.recyclerGchat.scrollToPosition(messageAdapter.itemCount - 1)
                }
            }
        }
    }

    private fun setupInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            v.setPadding(
                systemBarsInsets.left,
                systemBarsInsets.top,
                systemBarsInsets.right,
                imeInsets.bottom + systemBarsInsets.bottom
            )
            insets
        }
    }

    private fun generateChatId(userId1: String, userId2: String): String {
        return if (userId1 < userId2) {
            "$userId1-$userId2"
        } else {
            "$userId2-$userId1"
        }
    }

    private fun markMessagesAsSeen(messages: List<MessageData>) {
        messages.filter { it.senderId != currentUserId && !it.seenByReceiver }.forEach { message ->
            lifecycleScope.launch {
                messageViewModel.markMessageAsSeen(chatId, message.messageId)
            }
        }
    }

    override fun onStop() {
        super.onStop()
        lifecycleScope.launch {
            repository.updateTypingStatus(currentUserId, false)
        }
    }

    override fun onPause() {
        super.onPause()
        lifecycleScope.launch {
            repository.updateTypingStatus(currentUserId, false)
        }
    }

}
