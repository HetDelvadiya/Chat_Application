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
import com.awcindia.chatapplication.ViewModelFactory.AuthViewModelFactory
import com.awcindia.chatapplication.databinding.FragmentPhoneNumberBinding
import com.awcindia.chatapplication.model.UserPhoneNumber
import com.awcindia.chatapplication.repository.AuthRepository
import com.awcindia.chatapplication.viewmodel.AuthState
import com.awcindia.chatapplication.viewmodel.AuthViewModel
import com.google.firebase.auth.FirebaseAuth

class PhoneNumberFragment : Fragment() {

    private lateinit var binding: FragmentPhoneNumberBinding
    private lateinit var viewModel: AuthViewModel


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentPhoneNumberBinding.inflate(inflater, container, false)
        val auth = FirebaseAuth.getInstance()
        val repository = AuthRepository(auth)


        viewModel =
            ViewModelProvider(this, AuthViewModelFactory(repository))[AuthViewModel::class.java]

        binding.progressbar.visibility = View.INVISIBLE


        binding.next.setOnClickListener {
            val countryCode = binding.countryCode.text.toString()
            val phoneNumber = binding.phoneNumber.text.toString()

            UserPhoneNumber.phoneNumber = phoneNumber

            if (countryCode.isEmpty() || phoneNumber.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Please enter a valid phone number",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            val fullPhoneNumber = "+$countryCode$phoneNumber"

            viewModel.sendVerificationCode(fullPhoneNumber, requireActivity())
        }

        observeViewModel()

        return binding.root
    }

    private fun observeViewModel() {

        viewModel.authState.observe(viewLifecycleOwner) { state ->

            when (state) {

                is AuthState.Loading -> binding.progressbar.visibility = View.VISIBLE

                is AuthState.Success -> {

                    binding.progressbar.visibility = View.GONE
                    val navController = findNavController()
                    val bundle = Bundle()
                    bundle.putString("verificationId", viewModel.verificationId.value)
                    Log.d("verificationId", viewModel.verificationId.value.toString())

                    navController.navigate(
                        R.id.action_phoneNumberFragment_to_verificationFragment,
                        bundle
                    )
                }

                is AuthState.CodeSent -> {
                    binding.progressbar.visibility = View.GONE
                    val bundle = Bundle()
                    bundle.putString("verificationId", viewModel.verificationId.value)
                    Log.d("verificationId", viewModel.verificationId.value.toString())
                    findNavController().navigate(
                        R.id.action_phoneNumberFragment_to_verificationFragment,
                        bundle
                    )
                }

                is AuthState.Failure -> {
                    binding.progressbar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(),
                        "Authentication failed: ${state.error}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}
