package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.awcindia.chatapplication.databinding.FragmentCallBinding


class CallFragment : Fragment() {

    private lateinit var binding: FragmentCallBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentCallBinding.inflate(inflater, container, false)

        return binding.root
    }
}