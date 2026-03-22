package com.example.securescan.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.securescan.model.ApiResult
import com.example.securescan.model.AuthResponse
import com.example.securescan.model.UserResponse
import com.example.securescan.repository.MainRepository
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {
    // Single repository for the whole activity — same ApiService after QR scan (see MainRepository object).
    private val repository = MainRepository

    //LiveData to hold the user data, the UI listen to this


    private val mutableUserData = MutableLiveData<UserResponse?>()
    val userData: LiveData<UserResponse?> = mutableUserData

    private val mutableAuthResult = MutableLiveData<ApiResult<AuthResponse>?>()
    val authResult: LiveData<ApiResult<AuthResponse>?> = mutableAuthResult

    private val mutableIsLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = mutableIsLoading

    private val mutableErrorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = mutableErrorMessage

    /** One-shot: Level 6.2 — clear password on Auth after Retry from error screen. */
    private val mutableClearAuthPassword = MutableLiveData(false)
    val clearAuthPassword: LiveData<Boolean> = mutableClearAuthPassword

    private val mutableIsAuthenticated = MutableLiveData<Boolean>(false)
    val isAuthenticated: LiveData<Boolean> = mutableIsAuthenticated

    fun setAuthenticated(status: Boolean) {
        mutableIsAuthenticated.value = status
    }

    fun requestClearAuthPasswordForRetry() {
        mutableClearAuthPassword.value = true
    }

    fun consumeClearAuthPasswordRequest() {
        mutableClearAuthPassword.value = false
    }

    fun clearErrorMessage() {
        mutableErrorMessage.value = null
    }

    fun clearAuthResult() {
        mutableAuthResult.value = null
    }

    fun setErrorMessage(message: String?) {
        mutableErrorMessage.value = message
    }

    fun processQr(rawText: String) {
        mutableIsLoading.value = true
        mutableErrorMessage.value = null

        //viewModelScope run code in the background for not blocking the UI
        viewModelScope.launch {
            when (val result = repository.resolveQrData(rawText)) {
                is ApiResult.Success -> {
                    //save the user
                    mutableUserData.value = result.data
                    mutableIsLoading.value = false
                }

                is ApiResult.HttpError -> {
                    mutableErrorMessage.value =
                        result.message ?: "Server error (${result.code})"
                    mutableIsLoading.value = false
                }

                ApiResult.NetworkError -> {
                    mutableErrorMessage.value = "Network error. Check connection and try again."
                    mutableIsLoading.value = false
                }

                is ApiResult.ParseError -> {
                    mutableErrorMessage.value = result.message
                    mutableIsLoading.value = false
                }

                is ApiResult.ClientError -> {
                    mutableErrorMessage.value = result.message
                    mutableIsLoading.value = false
                }
            }
        }
    }


    fun login(password: String) {
        val currentUser = mutableUserData.value ?: return
        mutableIsLoading.value = true
        viewModelScope.launch {
            val result = repository.authenticateUser(currentUser.userId, password)
            mutableAuthResult.value = result
            mutableIsLoading.value = false
        }
    }

    /** Level 6.3 — new QR flow: drop resolve session and Retrofit client. */
    fun resetSession() {
        mutableUserData.value = null
        mutableAuthResult.value = null
        mutableErrorMessage.value = null
        mutableIsLoading.value = false
        consumeClearAuthPasswordRequest()
        repository.clearApiSession()
    }
}
