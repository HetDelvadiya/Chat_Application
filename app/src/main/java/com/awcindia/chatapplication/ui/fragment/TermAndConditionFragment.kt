package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.FragmentTermAndConditionBinding

class TermAndConditionFragment : Fragment() {

    private lateinit var binding: FragmentTermAndConditionBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentTermAndConditionBinding.inflate(inflater, container, false)


        binding.accept.setOnClickListener {
            val navController = findNavController()
            navController.navigate(R.id.action_termAndConditionFragment_to_phoneNumberFragment)
        }


        return binding.root
    }
}