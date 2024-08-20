package com.awcindia.chatapplication.ui.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.ViewModelFactory.MessageFactory
import com.awcindia.chatapplication.databinding.ActivityPersonsChatBinding
import com.awcindia.chatapplication.model.MessageData
import com.awcindia.chatapplication.repository.MassageRepository
import com.awcindia.chatapplication.ui.adapter.MessageAdapter
import com.awcindia.chatapplication.ui.viewmodel.ReceiverViewModel
import com.awcindia.chatapplication.ui.viewmodel.SenderViewModel
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MessageChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonsChatBinding
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var senderRoom: String
    private lateinit var receiverRoom: String
    private lateinit var currentUserId: String
    private val messageList: ArrayList<MessageData> = ArrayList()

    private lateinit var senderViewModel: SenderViewModel
    private lateinit var receiverViewModel: ReceiverViewModel

    private val firestore = FirebaseFirestore.getInstance()
    val repository = MassageRepository(firestore)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())
            val navigationBarHeight = systemBarsInsets.bottom
            // Adjust padding based on keyboard visibility
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                imeInsets.bottom + navigationBarHeight
            )
            insets
        }

        binding.progressBar.visibility = View.VISIBLE

        val receiverUID = intent.getStringExtra("userId").toString()
        val userName = intent.getStringExtra("userName")
        val userImage = intent.getStringExtra("userImage")

        currentUserId = FirebaseAuth.getInstance().currentUser?.uid.toString()
        senderRoom = currentUserId + receiverUID
        receiverRoom = receiverUID + currentUserId


        messageAdapter = MessageAdapter(this, messageList)
        binding.recyclerGchat.layoutManager = LinearLayoutManager(this@MessageChatActivity)
        binding.recyclerGchat.adapter = messageAdapter

        binding.contactName.text = userName

        Glide.with(this).load(userImage).placeholder(R.drawable.default_dp)
            .error(R.drawable.default_dp).into(binding.profileImage)


        senderViewModel =
            ViewModelProvider(this, MessageFactory(repository))[SenderViewModel::class.java]
        receiverViewModel =
            ViewModelProvider(this, MessageFactory(repository))[ReceiverViewModel::class.java]


        // Observe user status and typing status
        val combinedStatus = MutableLiveData<String>()

        val statusObserver = Observer<String> { status ->
            val typingStatus = if (combinedStatus.value?.contains("Typing...") == true) {
                "Typing..."
            } else {
                ""
            }
            binding.statusIndicatore.text = when (status) {
                "online" -> "${typingStatus}"
                else -> "${typingStatus}"
            }
        }

        repository.getUserTypingStatus(receiverUID).observe(this) { isTyping ->
            val currentStatus = combinedStatus.value ?: ""
            combinedStatus.value = if (isTyping) {
                if (currentStatus.contains("Typing...")) currentStatus else "$currentStatus Typing..."
            } else {
                currentStatus.replace("Typing...", "").trim()
            }
            statusObserver.onChanged(combinedStatus.value ?: "")
        }

        receiverViewModel.receiveMassage(senderRoom)

        receiverViewModel.messages.observe(this, Observer { messages ->
            binding.progressBar.visibility = View.INVISIBLE
            messageList.clear()
            messageList.addAll(messages)
            messageAdapter.notifyDataSetChanged()
            binding.recyclerGchat.scrollToPosition(messageList.size - 1)
        })


        binding.sendText.setOnClickListener {
            val messageText = binding.editGchatMessage.text.toString().trim()
            if (messageText.isNotEmpty()) {
                val message = MessageData(
                    massageId = firestore.collection("messages").document().id,
                    massage = messageText,
                    senderId = currentUserId,
                    timestamp = System.currentTimeMillis(),
                    userStatus = "Typing..."
                )

                senderViewModel.sendMessage(senderRoom, receiverRoom, message)
                // Immediately add the message to the adapter
                messageList.add(message)
                messageAdapter.notifyItemInserted(messageList.size - 1)
                binding.editGchatMessage.text?.clear()
                binding.recyclerGchat.scrollToPosition(messageList.size - 1)
            }
        }

        // Set typing status on text change
        binding.editGchatMessage.addTextChangedListener {
            repository.updateTypingStatus(currentUserId, it.toString().isNotEmpty())
        }
    }

    // Remove typing status when the activity is destroyed
    override fun onDestroy() {
        super.onDestroy()
        repository.updateTypingStatus(currentUserId, false)
    }
}
