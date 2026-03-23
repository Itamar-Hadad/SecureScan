package com.example.securescan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.example.securescan.R
import com.example.securescan.databinding.FragmentMainAppBinding

class MainAppFragment : Fragment() {

    private lateinit var binding: FragmentMainAppBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainAppBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews()
        handleBackPress()

        // load the home fragment by default
        if (savedInstanceState == null) {
            replaceFragment(HomeFragment())
        }
    }

    private fun initViews() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.mainAppBottomNav) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(v.paddingLeft, v.paddingTop, v.paddingRight, 0)
            insets
        }
        binding.mainAppBottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    replaceFragment(HomeFragment())
                    true
                }
                R.id.nav_device_info -> {
                    replaceFragment(DeviceInfoFragment())
                    true
                }
                else -> false
            }
        }
    }

    // function to replace the fragments inside the container
    private fun replaceFragment(fragment: Fragment) {
        childFragmentManager.beginTransaction()
            .replace(R.id.main_app_child_container, fragment)
            .setReorderingAllowed(true)
            .commit()
    }

    private fun handleBackPress() {
        // in the android there is always bar on the bottom of the screen that the user can go back.
        // I don't want that the user can go back to the auth screen after he already authenticated so
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // close the activity that we have been before
                requireActivity().finish()
            }
        }

        // connect the call back to the system navigation
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, callback)
    }
}