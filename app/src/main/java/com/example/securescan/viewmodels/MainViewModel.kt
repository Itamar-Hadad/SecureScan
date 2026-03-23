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


    private val mutableUserData = MutableLiveData<UserResponse?>() //only the viewModel can change this
    val userData: LiveData<UserResponse?> = mutableUserData //the UI can listen to this

    private val mutableAuthResult = MutableLiveData<ApiResult<AuthResponse>?>()
    val authResult: LiveData<ApiResult<AuthResponse>?> = mutableAuthResult

    private val mutableIsLoading = MutableLiveData<Boolean>(false)
    val isLoading: LiveData<Boolean> = mutableIsLoading

    private val mutableErrorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = mutableErrorMessage

    private val mutableClearAuthPassword = MutableLiveData(false)
    val clearAuthPassword: LiveData<Boolean> = mutableClearAuthPassword

    private val mutableIsAuthenticated = MutableLiveData(false)
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

    fun userFacingErrorMessage(result: ApiResult<*>): String {
        return when (result) {
            is ApiResult.HttpError -> result.message ?: "Server error (${result.code})"
            ApiResult.NetworkError -> "Network error. Check connection and try again."
            is ApiResult.ParseError -> result.message
            is ApiResult.ClientError -> result.message
            else -> "Something went wrong. Please try again."
        }
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
                    mutableErrorMessage.value = userFacingErrorMessage(result)
                    mutableIsLoading.value = false
                }

                is ApiResult.NetworkError -> {
                    mutableErrorMessage.value = userFacingErrorMessage(result)
                    mutableIsLoading.value = false
                }

                is ApiResult.ParseError -> {
                    mutableErrorMessage.value = userFacingErrorMessage(result)
                    mutableIsLoading.value = false
                }

                is ApiResult.ClientError -> {
                    mutableErrorMessage.value = userFacingErrorMessage(result)
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


    fun resetSession() {
        mutableUserData.value = null
        mutableAuthResult.value = null
        mutableErrorMessage.value = null
        mutableIsLoading.value = false
        mutableIsAuthenticated.value = false
        consumeClearAuthPasswordRequest()
        repository.clearApiSession()
    }
}
