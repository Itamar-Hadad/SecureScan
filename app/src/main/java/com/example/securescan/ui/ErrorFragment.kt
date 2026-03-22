package com.example.securescan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.securescan.R
import com.example.securescan.databinding.FragmentErrorBinding
import com.example.securescan.viewmodels.MainViewModel


class ErrorFragment : Fragment() {

    private lateinit var binding: FragmentErrorBinding
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentErrorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        observeViewModel()
    }

    private fun initViews() {
        binding.authErrBTNRetry.setOnClickListener {
            sharedViewModel.requestClearAuthPasswordForRetry()
            findNavController().popBackStack()
        }

        binding.authErrBTNScanQr.setOnClickListener {

            sharedViewModel.resetSession()

            findNavController().popBackStack(R.id.qrScanFragment, false)
        }
    }

    private fun observeViewModel() {
        sharedViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            binding.authErrLBLMessage.text = message ?: "Authentication failed. Please try again."
        }
    }
}

