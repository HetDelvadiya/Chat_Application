package com.awcindia.chatapplication.ui.activity

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivityPersonsChatBinding
import com.bumptech.glide.Glide

class PersonsChatActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPersonsChatBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityPersonsChatBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            // Adjust padding based on keyboard visibility
            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                imeInsets.bottom
            )
            insets
        }

        val userId = intent.getStringExtra("userId")
        val userName = intent.getStringExtra("userName")
        val userImage = intent.getStringExtra("userImage")
        binding.contactName.text = userName

        Glide.with(this)
            .load(userImage)
            .placeholder(R.drawable.default_dp) // Placeholder image while loading
            .error(R.drawable.default_dp) // Error image if loading fails
            .into(binding.profileImage)


    }
}