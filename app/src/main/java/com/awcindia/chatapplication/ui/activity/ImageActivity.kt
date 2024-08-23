package com.awcindia.chatapplication.ui.activity

import android.app.Activity
import android.content.DialogInterface
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.ActivityImageBinding

class ImageActivity : AppCompatActivity() {

    lateinit var binding: ActivityImageBinding
    private lateinit var getImageFromGallery: ActivityResultLauncher<Intent>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityImageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        dialogBox()

        // Initialize the gallery launcher
        getImageFromGallery =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val selectedImageUri = result.data?.data

                    binding.image.setImageURI(selectedImageUri)
                }
            }
    }

    private fun dialogBox() {

        AlertDialog.Builder(this).setTitle("Choose Image Source")
            .setPositiveButtonIcon(resources.getDrawable(R.drawable.photo_camera))
            .setPositiveButton("Camera", DialogInterface.OnClickListener { dialog, which ->
                openCamera()
            })
            .setNegativeButtonIcon(resources.getDrawable(R.drawable.gallery))
            .setNegativeButton("Gallery", OnClickListener { dialog, which ->
                openGallery()
            }).create().show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        getImageFromGallery.launch(intent)
    }

    private fun openCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(packageManager) != null) {
            startActivityForResult(intent, RESULT_OK)
        }
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {

            val imageBitmap = data?.extras?.get("data") as Bitmap
            binding.image.setImageBitmap(imageBitmap)
        }
    }
}