package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.FragmentStatusBinding


class StatusFragment : Fragment() {

    lateinit var binding: FragmentStatusBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {

        binding =  FragmentStatusBinding.inflate(inflater, container, false)

        return binding.root
    }

}