package com.awcindia.chatapplication.ui.activity


import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Spannable
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Suppress("SameParameterValue")
@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    lateinit var binding : ActivitySplashBinding
    private val auth = FirebaseAuth.getInstance()


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

        // Use a coroutine to delay for 2 seconds
        CoroutineScope(Dispatchers.Main).launch {
            delay(2000) // 2000 milliseconds (2 seconds) delay

            val currentUser = auth.currentUser
            val intent = if (currentUser != null) {
                Intent(this@SplashActivity, MainActivity::class.java)
            } else {
                Intent(this@SplashActivity, AuthActivity::class.java)
            }

            startActivity(intent)
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
        colorArray.forEachIndexed { _ , colorId ->
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