package com.example.securescan.model

import com.google.gson.annotations.SerializedName

data class QrConfig(
    // This prevents the app from breaking when the code is minimized (obfuscated) for release.
    @SerializedName("baseUrl")
    val baseUrl: String, //The Address from the QR
    @SerializedName("token")
    val token: String // The Token from the QR to recognize the user
)
