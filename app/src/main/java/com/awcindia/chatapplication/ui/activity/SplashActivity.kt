package com.awcindia.chatapplication.ui.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext


@Suppress("SameParameterValue")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    lateinit var binding: ActivitySplashBinding
    private var auth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // 2000 milliseconds (2 seconds) delay

            val currentUser = auth.currentUser
            if (currentUser != null) {
                // Fetch user profile data from Firestore
                val firestore = FirebaseFirestore.getInstance()
                val documentSnapshot = withContext(Dispatchers.IO) {
                    firestore.collection("users").document(currentUser.uid).get().await()
                }

                val userName = documentSnapshot.getString("userName") ?: ""

                // Check if the profile is filled
                val intent = if (userName.isNotEmpty()) {
                    Intent(this@SplashActivity, MainActivity::class.java)
                } else {
                    Intent(this@SplashActivity, AuthActivity::class.java).apply {
                        putExtra("navigate_to_set_profile", true)
                    }
                }

                startActivity(intent)
            } else {
                // User not authenticated, go to AuthActivity
                val intent = Intent(this@SplashActivity, AuthActivity::class.java)
                startActivity(intent)
            }

            finish() // Close SplashActivity so it won't remain in the back stack
        }


        val title = "From \n HetPatel"

        setColorOnTitle(title)
    }

    private fun setColorOnTitle(titleText: String) {
        val spannableString = SpannableString(titleText)

        val colorArray = arrayOf(
            R.color.color_part1, // Color for 'F'
            R.color.color_part2, // Color for 'r'
            R.color.color_part3, // Color for 'o'
            R.color.color_part1, // Color for 'm'
            R.color.color_part2, // Color for space
            R.color.color_part3, // Color for '\n'
            R.color.color_part1, // Color for 'H'
            R.color.color_part2, // Color for 'e'
            R.color.color_part3, // Color for 't'
            R.color.color_part1, // Color for 'P'
            R.color.color_part2, // Color for 'a'
            R.color.color_part3, // Color for 't'
            R.color.color_part1, // Color for 'e'
            R.color.color_part2, // Color for 'l'
        )

        var start = 0
        colorArray.forEachIndexed { _, colorId ->
            val color = resources.getColor(colorId)
            spannableString.setSpan(
                ForegroundColorSpan(color),
                start, start + 2, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            start++
        }
        binding.fromText.text = spannableString

    }
}