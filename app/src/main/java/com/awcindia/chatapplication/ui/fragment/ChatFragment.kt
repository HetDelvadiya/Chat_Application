package com.awcindia.chatapplication.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.ViewModelFactory.ContactViewModelFactory
import com.awcindia.chatapplication.adapter.ChatListAdapter
import com.awcindia.chatapplication.databinding.FragmentChatBinding
import com.awcindia.chatapplication.model.Contact
import com.awcindia.chatapplication.repository.ContactRepository
import com.awcindia.chatapplication.utils.ContactsManager
import com.awcindia.chatapplication.viewmodel.ContactViewModel
import com.google.firebase.firestore.FirebaseFirestore
import timber.log.Timber

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private var deviceContact: List<Contact> = arrayListOf()
    private lateinit var contactsManager: ContactsManager
    private lateinit var contactAdapter: ChatListAdapter
    private lateinit var viewModel: ContactViewModel
    private val firestore = FirebaseFirestore.getInstance()
    private val REQUEST_READ_CONTACTS = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        contactsManager = ContactsManager(requireContext())
        contactAdapter = ChatListAdapter()
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerview.adapter = contactAdapter

        if (isContactsPermissionGranted()) {
            initializeViewModelAndAdapter()
        } else {
            requestContactsPermission()
        }

        return binding.root
    }

    private fun initializeViewModelAndAdapter() {
        binding.progressbar.visibility = View.VISIBLE
        deviceContact = contactsManager.getContacts()
        Timber.d("list: %s", deviceContact.toString())

        val repository = ContactRepository(firestore, requireContext())
        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(repository)
        )[ContactViewModel::class.java]

        viewModel.getAllUser().observe(viewLifecycleOwner) { userList ->
            binding.progressbar.visibility = View.INVISIBLE
            contactAdapter.submitList(userList.distinct())
        }
    }

    private fun isContactsPermissionGranted(): Boolean {
        return ContextCompat.checkSelfPermission(
            requireContext(),
            android.Manifest.permission.READ_CONTACTS
        ) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestContactsPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.READ_CONTACTS
            )
        ) {
            // Show an explanation to the user
            // You can show a dialog explaining why you need this permission
        } else {
            // Request the permission
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(android.Manifest.permission.READ_CONTACTS),
                REQUEST_READ_CONTACTS
            )
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing contacts
                initializeViewModelAndAdapter()
            } else {
                // Permission denied, handle accordingly
                binding.progressbar.visibility = View.INVISIBLE
            }
        }
    }
}
