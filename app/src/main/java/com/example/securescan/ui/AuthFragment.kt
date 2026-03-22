package com.example.securescan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.securescan.R
import com.example.securescan.databinding.FragmentAuthBinding
import com.example.securescan.model.ApiResult
import com.example.securescan.utilities.SignalManager
import com.example.securescan.viewmodels.MainViewModel

class AuthFragment : Fragment() {
    private lateinit var binding: FragmentAuthBinding

    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAuthBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.authBTNBack.setOnClickListener {
            findNavController().navigateUp()
        }

        sharedViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.authLBLEmail.text = it.email
                binding.authLBLWelcome.text = "Welcome, ${it.fullName}!"
            }
        }

        binding.authEDTPassword.addTextChangedListener {
            val password = it.toString()
            binding.authBTNSignIn.isEnabled = password.length >= 4
        }

        binding.authBTNSignIn.setOnClickListener {
            val password = binding.authEDTPassword.text.toString()
            sharedViewModel.login(password)
        }

    }

    private fun observeViewModel() {
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.authPRBLoading.visibility = View.VISIBLE
                binding.authBTNSignIn.text = ""
            } else {
                binding.authPRBLoading.visibility = View.GONE
                binding.authBTNSignIn.text = getString(R.string.auth_sign_in)
            }
        }

        sharedViewModel.clearAuthPassword.observe(viewLifecycleOwner) { shouldClear ->
            if (shouldClear == true) {
                binding.authEDTPassword.setText("")
                sharedViewModel.consumeClearAuthPasswordRequest()
            }
        }

        sharedViewModel.authResult.observe(viewLifecycleOwner) { result ->
            when (result) {
                null -> return@observe
                is ApiResult.Success -> {
                    sharedViewModel.clearAuthResult()
                    findNavController().navigate(R.id.action_authFragment_to_successFragment)
                }
                is ApiResult.HttpError -> {
                    sharedViewModel.clearAuthResult()
                    sharedViewModel.setErrorMessage(result.message ?: "Server error (${result.code})")
                    findNavController().navigate(R.id.action_authFragment_to_errorFragment)
                    SignalManager.getInstance().vibrate()
                }
                ApiResult.NetworkError -> {
                    sharedViewModel.clearAuthResult()
                    sharedViewModel.setErrorMessage("Network error. Check connection and try again.")
                    findNavController().navigate(R.id.action_authFragment_to_errorFragment)
                    SignalManager.getInstance().vibrate()
                }
                is ApiResult.ParseError -> {
                    sharedViewModel.clearAuthResult()
                    sharedViewModel.setErrorMessage(result.message)
                    findNavController().navigate(R.id.action_authFragment_to_errorFragment)
                    SignalManager.getInstance().vibrate()
                }
                is ApiResult.ClientError -> {
                    sharedViewModel.clearAuthResult()
                    sharedViewModel.setErrorMessage(result.message)
                    findNavController().navigate(R.id.action_authFragment_to_errorFragment)
                    SignalManager.getInstance().vibrate()
                }
            }
        }
    }
}








