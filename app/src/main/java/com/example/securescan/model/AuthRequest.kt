package com.example.securescan.model

import com.google.gson.annotations.SerializedName

data class AuthRequest(
    @SerializedName("user_id")
    val userId: String,

    @SerializedName("password")
    val password: String
)
