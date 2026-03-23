package com.example.securescan.model

import com.google.gson.annotations.SerializedName

data class AuthResponse(
    @SerializedName("authenticated")
    val authenticated: Boolean? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("message")
    val message: String? = null,

    @SerializedName("error")
    val error: String? = null
)
