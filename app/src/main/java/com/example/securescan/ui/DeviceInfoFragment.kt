package com.example.securescan.ui

import android.content.pm.PackageInfo
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.securescan.databinding.FragmentDeviceInfoBinding
import java.util.Locale

class DeviceInfoFragment : Fragment() {

    private lateinit var binding: FragmentDeviceInfoBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeviceInfoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        // 1. דגם המכשיר והיצרן (מתוך Build)
        // model and manufacturer of the device (from Build)
        binding.deviceInfoLBLDeviceModelValue.text = Build.MODEL
        binding.deviceInfoLBLManufacturerValue.text = Build.MANUFACTURER.replaceFirstChar { it.uppercase() }

        // operating system and version (from Build)
        binding.deviceInfoLBLOsValue.text = "Android"
        binding.deviceInfoLBLOsVersionValue.text = "${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})"

        // app version (from package manager)
        binding.deviceInfoLBLAppVersionValue.text = getAppVersionInfo()

        // language and locale (from Locale)
        val currentLocale = Locale.getDefault()
        binding.deviceInfoLBLLanguageLocaleValue.text =
            "${currentLocale.displayLanguage} / ${currentLocale.displayCountry}"
    }

    //function that help us to get the app version from the package manager
    private fun getAppVersionInfo(): String {
        return try {
            val context = requireContext()
            val packageInfo: PackageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            "${packageInfo.versionName} (${packageInfo.versionCode})"
        } catch (e: Exception) {
            "Version unavailable"
        }
    }


}