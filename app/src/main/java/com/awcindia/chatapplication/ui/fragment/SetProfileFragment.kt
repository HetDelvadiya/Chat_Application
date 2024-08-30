package com.awcindia.chatapplication.ui.fragment

import android.Manifest
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.ViewModelFactory.SetProfileFactory
import com.awcindia.chatapplication.databinding.FragmentSetProfileBinding
import com.awcindia.chatapplication.repository.SetProfileRepository
import com.awcindia.chatapplication.viewmodel.SetProfitViewModel
import com.awcindia.chatapplication.viewmodel.UploadState
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class SetProfileFragment : Fragment() {

    private lateinit var binding: FragmentSetProfileBinding

    var userName : String = ""
    private var userId = FirebaseAuth.getInstance().currentUser?.uid

    private lateinit var viewModel: SetProfitViewModel
    private var imageUri: Uri? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSetProfileBinding.inflate(inflater, container, false)

        binding.progressbar.visibility = View.INVISIBLE // Make sure it's invisible initially

        val auth = FirebaseAuth.getInstance()
        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance()

        val repository = SetProfileRepository(firestore, storage)
        val viewModelFactory = SetProfileFactory(repository, auth)
        viewModel = ViewModelProvider(this, viewModelFactory)[SetProfitViewModel::class.java]

        binding.progressbar.visibility = View.INVISIBLE

        binding.addImage.setOnClickListener {
            checkPermissions()
        }

        binding.save.setOnClickListener {
           userName = binding.userName.text.toString()
            viewModel.saveUserProfile(userName, imageUri)
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {

        viewModel.uploadState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is UploadState.Loading -> binding.progressbar.visibility = View.VISIBLE
                is UploadState.Success -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(context, "Profile set successfully", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_setProfileFragment_to_mainActivity)
                    requireActivity().finish()
                }

                is UploadState.Error -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(context, state.message, Toast.LENGTH_LONG).show()
                }
            }
        }
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

    private val pickImageLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK && result.data != null) {
                imageUri = result.data?.data
                binding.profileImage.setImageURI(imageUri)
            }
        }

    private val requestPermissionLauncher =
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
}