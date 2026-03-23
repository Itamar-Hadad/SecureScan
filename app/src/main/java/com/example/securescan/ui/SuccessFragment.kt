package com.example.securescan.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.example.securescan.R
import com.example.securescan.databinding.FragmentSuccessBinding
import com.example.securescan.viewmodels.MainViewModel


class SuccessFragment : Fragment() {

    private lateinit var binding: FragmentSuccessBinding
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSuccessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        markAsAuthenticated()
        startAutoAdvance()
    }

    private fun initViews() {
        sharedViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.successLBLName.text = it.fullName
                binding.successLBLEmail.text = it.email
            }
        }
    }

    private fun markAsAuthenticated() {
       //update the shared view model to mark the user as authenticated
        sharedViewModel.setAuthenticated(true)
    }

    private fun startAutoAdvance() {
        //wait 2 sec before navigating to main app
        Handler(Looper.getMainLooper()).postDelayed({
            if (isAdded) {
                navigateToMainApp()
            }
        }, 2000)
    }

    private fun navigateToMainApp() {
        // define NavOptions to remove all Auth fragments from the back stack
        val navOptions = NavOptions.Builder()
            .setPopUpTo(R.id.qrScanFragment, true)
            .build()

        findNavController().navigate(
            R.id.action_successFragment_to_mainAppFragment,
            null,
            navOptions
        )
    }
}

