package com.example.securescan.model

import com.google.gson.annotations.SerializedName

/**
 * Matches the company API: success body is typically `{ "authenticated": true }`.
 * Optional fields cover legacy or alternate payloads without changing the server.
 */
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
