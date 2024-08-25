package com.awcindia.chatapplication.ui.activity

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivityImageMessageBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class ImageMessageActivity : AppCompatActivity() {


    lateinit var binding: ActivityImageMessageBinding
    private var fileUri: Uri? = null
    private lateinit var startForProfileImageResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityImageMessageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        startForProfileImageResult =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
                val resultCode = result.resultCode
                val data = result.data
                when (resultCode) {
                    Activity.RESULT_OK -> {
                        // Image Uri will not be null for RESULT_OK
                        fileUri = data?.data
                        if (fileUri != null) {
                            binding.imageMessage.setImageURI(fileUri)
                        }
                    }

                    ImagePicker.RESULT_ERROR -> {
                        Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                    }

                    else -> {
                        Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            }

        ImagePicker.with(this)
            .crop()
            .maxResultSize(
                1080,
                1080
            )  // Final image resolution will be less than 1080 x 1080(Optional)
            .createIntent { intent ->
                startForProfileImageResult.launch(intent)
            }

        binding.sentImage.setOnClickListener {
            binding.progressBar.visibility = View.VISIBLE
            fileUri?.let { uri ->
                CoroutineScope(Dispatchers.IO).launch {
                    withContext(Dispatchers.Main) {

                    }
                    val imageUrl = uploadImageToFirebaseStorage(uri)
                    withContext(Dispatchers.Main) {
                        binding.progressBar.visibility = View.INVISIBLE
                        if (imageUrl != null) {
                            val intent =
                                Intent(this@ImageMessageActivity, MessageChatActivity::class.java)
                            intent.putExtra("imageUri", imageUrl) // Pass the Firebase Storage URL
                            setResult(Activity.RESULT_OK, intent)
                            finish()
                        } else {
                            Toast.makeText(
                                this@ImageMessageActivity,
                                "Upload failed",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }
    }

    private suspend fun uploadImageToFirebaseStorage(uri: Uri): String? {
        val storageRef = FirebaseStorage.getInstance().reference
        val imageRef = storageRef.child("images/${uri.lastPathSegment}")

        return try {
            imageRef.putFile(uri).await()
            val downloadUri = imageRef.downloadUrl.await()
            downloadUri.toString()
        } catch (e: Exception) {
            null
        }
    }
}
