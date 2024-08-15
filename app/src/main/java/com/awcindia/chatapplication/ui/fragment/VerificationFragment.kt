package com.awcindia.chatapplication.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.awcindia.chatapplication.R
import com.awcindia.chatapplication.databinding.FragmentVerificationBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await


class VerificationFragment : Fragment() {


    private lateinit var binding: FragmentVerificationBinding

    private lateinit var auth: FirebaseAuth
    private var verificationId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentVerificationBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()
        binding.progressbar.visibility = View.INVISIBLE

        verificationId = arguments?.getString("verificationId")

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

            lifecycleScope.launch {
                try {
                    verificationId?.let { id ->
                        verifyCode(id, code)
                    }
                } catch (e: Exception) {
                    Toast.makeText(
                        requireContext(),
                        "Verification failed: ${e.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }

        return binding.root
    }

    private suspend fun verifyCode(verificationId: String, code: String) {
        val credential = PhoneAuthProvider.getCredential(verificationId, code)
        binding.progressbar.visibility = View.VISIBLE
        signInWithPhoneAuthCredential(credential)
    }

    private suspend fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        try {
            val task = auth.signInWithCredential(credential).await()
            if (task.user != null) {
                Toast.makeText(requireContext(), "Authentication successful!", Toast.LENGTH_LONG)
                    .show()
                findNavController().navigate(R.id.action_verificationFragment_to_setProfileFragment)
            } else {
                throw Exception("Authentication failed")
            }
        } catch (e: Exception) {
            Log.w("PhoneAuth", "signInWithCredential:failure", e)
            Toast.makeText(
                requireContext(),
                "Authentication failed: ${e.message}",
                Toast.LENGTH_LONG
            ).show()
        } finally {
            binding.progressbar.visibility = View.GONE
        }
    }
}