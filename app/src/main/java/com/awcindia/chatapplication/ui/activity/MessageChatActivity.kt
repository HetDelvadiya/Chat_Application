package com.awcindia.chatapplication.ui.activity


import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.ViewModelFactory.CallViewModelFactory
import com.awcindia.chatapplication.ViewModelFactory.MessageFactory
import com.awcindia.chatapplication.adapter.MessageAdapter
import com.awcindia.chatapplication.databinding.ActivityPersonsChatBinding
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository
import com.awcindia.chatapplication.viewmodel.CallViewModel
import com.awcindia.chatapplication.viewmodel.MessageViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.zegocloud.uikit.service.defines.ZegoUIKitUser
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonsChatBinding
    private lateinit var messageViewModel: MessageViewModel
    private lateinit var imageMessageLauncher: ActivityResultLauncher<Intent>
    private var fileUri: String? = null
    private lateinit var chatId: String
    private lateinit var messageAdapter: MessageAdapter
    private val repository = MassageRepository()
    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
    private var receiverId: String = ""
    private lateinit var callViewModel: CallViewModel
    var phoneNumber: String? = ""

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

        retrieveImage()
        binding.pickImage.setOnClickListener {

            val i = Intent(this, ImageMessageActivity::class.java)
            imageMessageLauncher.launch(i)
        }

        binding.contactName.text = userName
        Glide.with(this).load(userImage).placeholder(R.drawable.default_dp)
            .error(R.drawable.default_dp).into(binding.profileImage)

        messageAdapter = MessageAdapter(this, currentUserId)
        binding.recyclerGchat.adapter = messageAdapter
        binding.recyclerGchat.layoutManager = LinearLayoutManager(this)

        callViewModel =
            ViewModelProvider(this, CallViewModelFactory(application))[CallViewModel::class.java]

        callViewModel.setUpZeGoUIKit()
        callViewModel.getReceiverPhoneNumber(receiverId)

        callViewModel.receiverPhoneNumber.observe(this, Observer { phone ->
            if (phone != null) {
                Log.d("VoiceCallActivity", "Receiver's phone number: $phone")
                phoneNumber = phone
                startVideoCall(phoneNumber!!, phoneNumber!!)
                startVoiceCall(phoneNumber!!, phoneNumber!!)
            } else {
                Log.e("VoiceCallActivity", "Receiver's phone number is not available.")

            }
        })

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
            repository.getUserTypingStatus(receiverId).collectLatest { isTyping ->
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
                    imageUrl = "",
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

    private fun retrieveImage() {
        // Initialize ActivityResultLauncher
        imageMessageLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val imageUri = result.data?.getStringExtra("imageUri")
                    imageUri?.let {
                        fileUri = it
                        sendImageMessage(fileUri)
                    }
                }
            }
    }

    private fun sendImageMessage(fileUris: String?) {

        val message = MessageData(
            message = "",
            senderId = currentUserId,
            receiverId = receiverId,
            timestamp = System.currentTimeMillis(),
            messageType = "image",
            imageUrl = fileUris.toString(),
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

    private fun startVideoCall(targetUserId: String, targetUserName: String) {

        binding.videoCall.setIsVideoCall(true)
        binding.videoCall.resourceID = "zego_call"
        binding.videoCall.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetUserName)))
    }

    private fun startVoiceCall(targetUserId: String, targetUserName: String) {
        binding.voiceCall.setIsVideoCall(false)
        binding.voiceCall.resourceID = "zego_call"
        binding.voiceCall.setInvitees(listOf(ZegoUIKitUser(targetUserId, targetUserName)))
    }

    override fun onDestroy() {
        super.onDestroy()
        callViewModel.unInitZeGoUIKit()
    }

}
