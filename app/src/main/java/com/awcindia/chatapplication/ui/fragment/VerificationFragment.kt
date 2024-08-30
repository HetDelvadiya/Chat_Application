package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.ViewModelFactory.VerificationFactory
import com.awcindia.chatapplication.databinding.FragmentVerificationBinding
import com.awcindia.chatapplication.repository.AuthStates
import com.awcindia.chatapplication.repository.VerificationRepository
import com.awcindia.chatapplication.viewmodel.VerificationViewModel
import com.google.firebase.auth.FirebaseAuth


class VerificationFragment : Fragment() {


    private lateinit var binding: FragmentVerificationBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var viewModel: VerificationViewModel
    private var verificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {


        binding = FragmentVerificationBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        val repository = VerificationRepository(auth)
        val id = arguments?.getString("verificationId")
        verificationId = id
        Log.d("verificationId", verificationId.toString())

        viewModel = ViewModelProvider(
            this,
            VerificationFactory(repository)
        )[VerificationViewModel::class.java]


        binding.progressbar.visibility = View.INVISIBLE

        viewModel.authState.observe(viewLifecycleOwner) { authState ->
            handleAuthState(authState)
        }


        binding.verify.setOnClickListener {
            val code = binding.verificationCode.text.toString()

            if (code.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter the verification code",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            verificationId?.let {
                viewModel.verifyCode(it, code)
            }
        }

        return binding.root
    }

    private fun handleAuthState(authState: AuthStates) {
        when (authState) {

            is AuthStates.Loading -> {
                binding.progressbar.visibility = View.VISIBLE
            }

            is AuthStates.Authenticated -> {
                binding.progressbar.visibility = View.GONE
                findNavController().navigate(R.id.action_verificationFragment_to_setProfileFragment)
            }

            is AuthStates.Error -> {
                binding.progressbar.visibility = View.GONE
                Toast.makeText(
                    requireContext(),
                    "Authentication failed: ${authState.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}