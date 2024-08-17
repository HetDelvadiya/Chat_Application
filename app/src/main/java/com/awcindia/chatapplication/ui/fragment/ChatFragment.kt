package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.ViewModelFactory.ChatListFactory
import com.awcindia.chatapplication.databinding.FragmentChatBinding
import com.awcindia.chatapplication.repository.ChatListRepository
import com.awcindia.chatapplication.ui.adapter.ChatListAdapter
import com.awcindia.chatapplication.ui.viewmodel.ChatListViewModel
import com.google.firebase.firestore.FirebaseFirestore


class ChatFragment : Fragment() {

    lateinit var binding: FragmentChatBinding
    private lateinit var viewModel: ChatListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentChatBinding.inflate(inflater, container, false)

        val firestore = FirebaseFirestore.getInstance()
        val repository = ChatListRepository(firestore)
        viewModel =
            ViewModelProvider(this, ChatListFactory(repository))[ChatListViewModel::class.java]


        binding.chatRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        viewModel.getUserList()

        viewModel.userList.observe(viewLifecycleOwner, Observer {

                userList ->
            binding.chatRecyclerview.adapter = ChatListAdapter(userList)
        })

        return binding.root
    }
}