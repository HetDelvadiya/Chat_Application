package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.awcindia.chatapplication.ViewModelFactory.CallHistoryFactory
import com.awcindia.chatapplication.adapter.CallHistoryAdapter
import com.awcindia.chatapplication.databinding.FragmentCallBinding
import com.awcindia.chatapplication.model.database.CallHistoryDatabase
import com.awcindia.chatapplication.repository.CallHistoryRepository
import com.awcindia.chatapplication.viewmodel.CallHistoryViewModel


class CallFragment : Fragment() {

    private lateinit var binding: FragmentCallBinding
    lateinit var callHistoryViewModel: CallHistoryViewModel
    lateinit var callHistoryAdapter: CallHistoryAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentCallBinding.inflate(inflater, container, false)

        callHistoryAdapter = CallHistoryAdapter(requireContext())
        binding.callRecyclerview.adapter = callHistoryAdapter
        binding.callRecyclerview.layoutManager = LinearLayoutManager(requireContext())

        val doa = CallHistoryDatabase.getDatabase(requireContext()).callDoa()
        val callHistoryRepository = CallHistoryRepository(doa)
        callHistoryViewModel =
            ViewModelProvider(
                this,
                CallHistoryFactory(callHistoryRepository)
            )[CallHistoryViewModel::class.java]

        callHistoryViewModel.getCallHistory().observe(viewLifecycleOwner, Observer { callList ->
            callHistoryAdapter.submitList(callList)
        })
        return binding.root
    }
}