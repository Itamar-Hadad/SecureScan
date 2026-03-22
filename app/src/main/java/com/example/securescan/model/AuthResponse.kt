package com.example.securescan.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("status")
    val status: String, // for example: success, error

    @SerializedName("message")
    val message: String? // error message if there is any
)
