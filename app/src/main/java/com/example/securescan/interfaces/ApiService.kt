package com.example.securescan.interfaces

import com.example.securescan.model.AuthRequest
import com.example.securescan.model.AuthResponse
import com.example.securescan.model.UserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

/**
 * Retrofit is a type-safe HTTP client for Android and Java.
 * It simplifies networking by turning HTTP APIs into Kotlin interfaces.
 * * Key features:
 * 1. Automatically handles network requests (GET, POST, etc.).
 * 2. Uses Converters (like Gson) to transform JSON data directly into Kotlin objects.
 * 3. Supports Coroutines (suspend functions) for non-blocking network calls.
 */

interface ApiService {

    //suspend tells Kotlin that this function can be paused and resumed later.
    //It allows the network call to run in the background without freezing the app's UI.

    //QR decoding stage: send a token, receive user information
    @POST("qr/resolve")
    suspend fun resolveQr(
        @Body request: Map<String, String> // send {"qr_token": "..."}
    ): Response<UserResponse>

    //Authentication stage: send user credentials (user_id, password), receive approval or rejection.
    @POST("auth/validate")
    suspend fun validatePassword(
        @Body request: AuthRequest
    ): Response<AuthResponse>
}