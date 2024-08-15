package com.awcindia.chatapplication.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.FragmentSetProfileBinding
import com.awcindia.chatapplication.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID


class SetProfileFragment : Fragment() {

    private lateinit var binding: FragmentSetProfileBinding
    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance().reference
    private var imageUploaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetProfileBinding.inflate(inflater, container, false)

        binding.progressbar.visibility = View.INVISIBLE // Make sure it's invisible initially

        // Initialize Activity Result launcher
        pickImageLauncher =
            registerForActivityResult(
                ActivityResultContracts.StartActivityForResult(),
                ActivityResultCallback { result ->

                    if (result.resultCode == RESULT_OK && result.data != null) {
                        val uri = result.data!!.data

                        try {
                            val inputStream =
                                requireContext().contentResolver.openInputStream(uri!!)
                            val bitmap = BitmapFactory.decodeStream(inputStream)

                            binding.profileImage.setImageBitmap(bitmap)
                            binding.profileImage.tag = uri

                        } catch (e: Exception) {
                            Toast.makeText(
                                context,
                                "Failed to load image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                })

        // Initialize permission request launcher
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
                if (granted) {
                    pickImageFromGallery()
                } else {
                    Toast.makeText(
                        context,
                        "Permission denied to read your External storage",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }

        binding.addImage.setOnClickListener {
            checkPermissions()
        }

        binding.save.setOnClickListener {
            saveUserProfile()
            binding.progressbar.visibility =
                View.VISIBLE // Show the progress bar when the save process starts
            binding.save.setOnClickListener(null) // Disable the save button after it's clicked
        }
        return binding.root
    }


    // For permission Check
    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_MEDIA_IMAGES
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickImageFromGallery()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_MEDIA_IMAGES)
                }
            }
        } else {
            when {
                ContextCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED -> {
                    pickImageFromGallery()
                }

                else -> {
                    requestPermissionLauncher.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
                }
            }
        }
    }

    // For Image pick from gallery
    private fun pickImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun saveUserProfile() {
        val userName = binding.userName.text.toString()
        val userID = firebaseAuth.currentUser?.uid
        val userImage = binding.profileImage.tag as? Uri

        if (userName.isNotEmpty() && userImage != null) {
            if (imageUploaded) {
                Toast.makeText(context, "Image already uploaded", Toast.LENGTH_SHORT).show()
                return
            }

            val imageRef = storage.child("profileImage/${UUID.randomUUID()}")
            imageRef.putFile(userImage).addOnCompleteListener {
                imageRef.downloadUrl.addOnSuccessListener { downloadUri ->
                    val user = User(userID!!, userName, downloadUri.toString())

                    // Save User profile to Firestore
                    firestore.collection("users").document(userID).set(user).addOnCompleteListener {
                        Toast.makeText(context, "Profile set successfully", Toast.LENGTH_LONG)
                            .show()
                        val navController = findNavController()
                        navController.navigate(R.id.action_setProfileFragment_to_mainActivity)
                        requireActivity().finish()

                    }.addOnFailureListener {
                        Toast.makeText(context, "Failed to set profile", Toast.LENGTH_LONG).show()
                    }
                    imageUploaded = true  // Set flag after successful upload

                    // Hide the progress bar after completing the upload
                    binding.progressbar.visibility = View.VISIBLE

                }.addOnFailureListener { e ->
                    Toast.makeText(
                        context,
                        "Failed to upload image: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    // Hide the progress bar if there's a failure
                    binding.progressbar.visibility = View.INVISIBLE
                }
            }
        } else {
            Toast.makeText(context, "Please enter user name and select an image", Toast.LENGTH_LONG)
                .show()

            // Hide the progress bar if there's an error
            binding.progressbar.visibility = View.INVISIBLE
        }
    }
}