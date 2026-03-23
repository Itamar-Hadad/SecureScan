package com.example.securescan.utilities

import com.example.securescan.BuildConfig
import com.example.securescan.interfaces.ApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {

    // Avoid request/response bodies in logs to prevent leaking credentials.
    private val logging = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }

    //OkHttpClient is the underlying networking engine that handles the actual HTTP connections and data transfer.
    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder().apply {
            // Registers the logging interceptor to monitor network traffic (debug only — avoids logging passwords in release).
            if (BuildConfig.DEBUG) {
                addInterceptor(logging)
            }
        }.build()
    }

    //A factory method that builds a dynamic Retrofit instance using the Base URL from the QR scan.
    fun createService(baseUrl: String): ApiService {
        val normalized = if (baseUrl.endsWith("/")) baseUrl else "$baseUrl/"
        return Retrofit.Builder()
            .baseUrl(normalized) //the url from the qr
            .addConverterFactory(GsonConverterFactory.create()) //converts the json to kotlin objects (to the data class)
            .client(client)
            .build()

            .create(ApiService::class.java)
    }
}
