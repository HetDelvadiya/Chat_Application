package com.awcindia.chatapplication.ui.fragment

import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.ViewModelFactory.ContactViewModelFactory
import com.awcindia.chatapplication.databinding.FragmentChatBinding
import com.awcindia.chatapplication.model.Contact
import com.awcindia.chatapplication.repository.ContactRepository
import com.awcindia.chatapplication.ui.adapter.ChatListAdapter
import com.awcindia.chatapplication.ui.viewmodel.ContactViewModel
import com.awcindia.chatapplication.utils.ContactsManager
import com.google.firebase.firestore.FirebaseFirestore

class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private var deviceContacts: List<Contact> = arrayListOf()
    private lateinit var contactsManager: ContactsManager
    private lateinit var contactAdapter: ChatListAdapter
    private lateinit var viewModel: ContactViewModel
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)
        contactsManager = ContactsManager(requireContext())

        if (isContactsPermissionGranted()) {
            binding.progressbar.visibility = View.VISIBLE
            deviceContacts = contactsManager.getContacts()
           // Log.d("contacts", deviceContacts.toString())
        } else {
            requestContactsPermission()
        }

        Log.d("phoneNumbers", deviceContacts.toString())
        contactAdapter = ChatListAdapter()
        binding.chatRecyclerview.layoutManager = LinearLayoutManager(requireContext())
        binding.chatRecyclerview.adapter = contactAdapter

        val repository = ContactRepository(firestore)
        viewModel = ViewModelProvider(
            this,
            ContactViewModelFactory(repository)
        )[ContactViewModel::class.java]

        viewModel.getAllUser(deviceContacts).observe(viewLifecycleOwner) { userList ->
          contactAdapter.submitList(userList)
            binding.progressbar.visibility = View.INVISIBLE
        }

        return binding.root
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
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_READ_CONTACTS) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing contacts
                deviceContacts = contactsManager.getContacts()
                // Optionally, refresh the UI or data here
            } else {
                requestContactsPermission()
            }
        }
    }
    companion object {
        private const val REQUEST_READ_CONTACTS = 1
    }
}