package com.example.securescan.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.securescan.databinding.FragmentHomeBinding
import com.example.securescan.utilities.Constants
import com.example.securescan.viewmodels.MainViewModel
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.TimeZone


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private val sharedViewModel: MainViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        // get the user data from the shared view model
        sharedViewModel.userData.observe(viewLifecycleOwner) { user ->
            user?.let {
                binding.homeLBLFullNameValue.text = it.fullName
                binding.homeLBLEmailValue.text = it.email
                binding.homeLBLCompanyValue.text = it.company
                binding.homeLBLDepartmentValue.text = it.department
                binding.homeLBLUserIdValue.text = it.userId

                binding.homeLBLAccountCreationDateValue.text = formatDate(it.accountCreationDate)
            }
        }
    }

    private fun formatDate(dateString: String?): String {
        if (dateString.isNullOrEmpty()) return " "

        return try {
            // the format that we got from the server
            val parser = SimpleDateFormat(Constants.DateFormats.SERVER_ISO_UTC, Locale.getDefault())
            parser.timeZone = TimeZone.getTimeZone(Constants.DateFormats.UTC_TIMEZONE_ID)

            // the format i want to show the user
            val formatter = SimpleDateFormat(Constants.DateFormats.UI_READABLE, Locale.getDefault())
            val date = parser.parse(dateString)

            date?.let { formatter.format(it) } ?: dateString
        } catch (e: Exception) {
            dateString // if there is anm error we show the string from the server
        }
    }


}