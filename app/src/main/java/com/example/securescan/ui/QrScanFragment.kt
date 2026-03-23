package com.example.securescan.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage


import com.example.securescan.R
import com.example.securescan.databinding.FragmentQrScanBinding
import com.example.securescan.utilities.Constants
import com.example.securescan.utilities.SignalManager
import com.example.securescan.viewmodels.MainViewModel

class QrScanFragment : Fragment() {

    private lateinit var binding: FragmentQrScanBinding
    private var isScanning = true
    private val sharedViewModel: MainViewModel by activityViewModels()

    // flag that say if i wait for the server to respond
    private var awaitingResolveResult = false

    private var boundImageAnalysis: ImageAnalysis? = null


    //setting for the barcode scanner that he can scan only QR
    private val qrBarcodeScanner by lazy {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(Barcode.FORMAT_QR_CODE)
            .build()
        //create the barcode scanner
        BarcodeScanning.getClient(options)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted)
            startCamera()
        else
            Snackbar.make(
                binding.root,
                R.string.qr_scan_camera_denied,
                Snackbar.LENGTH_LONG
            )
                .setAction(R.string.qr_scan_settings) { openAppSettings() }
                .show()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentQrScanBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Fragment instance can survive on the back stack with isScanning == false after a scan;
        // when the view is created again we must allow new scans.
        isScanning = true
        awaitingResolveResult = false

        observeViewModel()
        checkPermissions()
    }

    override fun onResume() {
        super.onResume()
        // Back from Auth / Error: analyzer was cleared and never reattached; same instance may keep isScanning false.
        binding.root.post {
            if (!isAdded) return@post
            if (sharedViewModel.isLoading.value == true) return@post
            isScanning = true
            awaitingResolveResult = false
            boundImageAnalysis?.let { attachBarcodeAnalyzer() }
        }
    }

    private fun observeViewModel() {
        //listen to the loading state and show/hide the progress bar
        sharedViewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            val visible = if (isLoading) View.VISIBLE else View.GONE
            binding.qrScanPRBLoading.visibility = visible
            binding.qrScanLBLResolving.visibility = visible
        }

       //listen to the the user details that i get from the server
        sharedViewModel.userData.observe(viewLifecycleOwner) { user ->
            if (user != null && awaitingResolveResult) {
                awaitingResolveResult = false
                findNavController().navigate(R.id.action_qrScanFragment_to_authFragment)
            }
        }


        sharedViewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            message?.let {
                awaitingResolveResult = false
                isScanning = true
                Snackbar.make(binding.root, it, Snackbar.LENGTH_LONG)
                    .setAction(R.string.qr_scan_retry) {}
                    .addCallback(object : Snackbar.Callback() {
                        //when the snakebar is close this function clean the error message from the viewmodel
                        override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                            sharedViewModel.clearErrorMessage()
                            binding.root.post { attachBarcodeAnalyzer() }
                        }
                    })
                    .show()
            }
        }
    }

    private fun openAppSettings() {
        startActivity(
            Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                data = Uri.fromParts(
                    Constants.Intents.PACKAGE_SCHEME,
                    requireContext().packageName,
                    null
                )
            }
        )
    }

    private fun checkPermissions() {
        when {
            ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED -> startCamera()

            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                Snackbar.make(
                    binding.root,
                    R.string.qr_scan_camera_rationale,
                    Snackbar.LENGTH_INDEFINITE
                )
                    .setAction(R.string.qr_scan_allow) {
                        requestPermissionLauncher.launch(Manifest.permission.CAMERA)
                    }
                    .show()
            }

            else -> requestPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // set the preview what the user see
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.qrScanVIEWPreview.surfaceProvider)
                }

            // create the image analysis
            val imageAnalysis = ImageAnalysis.Builder()
                // STRATEGY_KEEP_ONLY_LATEST if the analysis is slow take the last frame
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()

            boundImageAnalysis = imageAnalysis
            attachBarcodeAnalyzer()

            // choose the back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                // disconnect the old use cases of the camera before rebinding
                cameraProvider.unbindAll()
                // connect the camera to the lifecycle of the fragment if the fragment close so the camera too
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageAnalysis
                )
            } catch (exc: Exception) {
                SignalManager
                    .getInstance(

                    ).toast("Use case binding failed",
                        SignalManager.ToastLength.SHORT)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun attachBarcodeAnalyzer() {
        if (!isAdded) return // if the fragment is not attached to the activity return
        val analysis = boundImageAnalysis ?: return

        //every frame that come from the camera send to the analyzer
        analysis.setAnalyzer(ContextCompat.getMainExecutor(requireContext())) { imageProxy ->
            processImageProxy(imageProxy)
        }
    }

    private fun onQrCodeDetected(qrText: String) {
        if (!isScanning) return

        isScanning = false
        awaitingResolveResult = true
        binding.root.post { boundImageAnalysis?.clearAnalyzer() }

        SignalManager
            .getInstance()
            .vibrate()

        sharedViewModel
            .processQr(qrText)
    }

    //function that get one frame and scan the qr from the frame

    private fun processImageProxy(imageProxy: ImageProxy) {

        if (!isScanning) {
            imageProxy.close()
            return
        }

        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            //create an inputimage for the ML kit
            val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)

            //send it to the ML kit
            qrBarcodeScanner.process(image)
                .addOnSuccessListener { barcodes ->
                    for (barcode in barcodes) {
                        //take the raw value of the barcode
                        val rawValue = barcode.rawValue
                        if (rawValue != null) {
                            onQrCodeDetected(rawValue)
                        }
                    }
                }
                .addOnFailureListener {
                    //if the ml kit failed in the scan we wait for the next frame
                }
                .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

}
