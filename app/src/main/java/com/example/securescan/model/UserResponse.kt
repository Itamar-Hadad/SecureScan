package com.example.securescan.model

import com.google.gson.annotations.SerializedName

//Data model representing the user information returned from the /qr/resolve endpoint.
data class UserResponse(
    // The server uses snake_case, but we map it to camelCase for clean Kotlin code.
    @SerializedName("full_name")
    val fullName: String,

    @SerializedName("email")
    val email: String,

    @SerializedName("company")
    val company: String,

    @SerializedName("department")
    val department: String,

    @SerializedName("user_id")
    val userId: String,

    @SerializedName("account_creation_date")
    val accountCreationDate: String,

    @SerializedName("account_status")
    val accountStatus: String,

    // This field can be null if the user has never logged in before.
    @SerializedName("last_login_time")
    val lastLoginTime: String?
)