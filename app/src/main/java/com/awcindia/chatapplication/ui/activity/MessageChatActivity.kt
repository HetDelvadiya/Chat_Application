package com.awcindia.chatapplication.ui.activity


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
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
import com.google.firebase.firestore.FirebaseFirestore

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonsChatBinding
    private lateinit var messageViewModel: MessageViewModel

    private lateinit var chatId: String
    private lateinit var messageAdapter: MessageAdapter
    private val firestore = FirebaseFirestore.getInstance()
    private val repository = MassageRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var receiverId: String = ""

    override fun onStart() {
        super.onStart()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupInsets()

        binding.progressBar.visibility = View.INVISIBLE
        messageViewModel =
            ViewModelProvider(this, MessageFactory(repository)).get(MessageViewModel::class.java)


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
            }
        }


        // Observe typing status
        messageViewModel.getTypingStatus(chatId).observe(this) { typingStatus ->
            updateTypingIndicator(typingStatus)
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
//                binding.recyclerGchat.scrollToPosition()
                binding.editGchatMessage.text?.clear()
            }
        }

        // Set typing status when user types
        binding.editGchatMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                messageViewModel.setTypingStatus(chatId, currentUserId, s.isNullOrEmpty().not())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }


    private fun updateTypingIndicator(typingStatus: Map<String, Boolean>?) {
        val isTyping = typingStatus?.get(receiverId) == true
        // Show typing indicator based on `isTyping`
        binding.typingIndicator.visibility = if (isTyping) View.VISIBLE else View.GONE
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

}
