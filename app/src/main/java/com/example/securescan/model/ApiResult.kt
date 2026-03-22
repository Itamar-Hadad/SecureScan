package com.example.securescan.model

// ApiResult is a sealed class that represents all possible outcomes of an API call.
// Instead of returning only data or throwing exceptions, we return a clear result type.
sealed class ApiResult<out Payload> {

    // Success: the API call succeeded and returned valid data.
    // data contains the response from the server (UserResponse, AuthResponse)
    data class Success<Payload>(val data: Payload) : ApiResult<Payload>()

    // HttpError: the server responded, but with an error status code (not 2xx)
    data class HttpError(val code: Int, val message: String?) : ApiResult<Nothing>()

    // NetworkError: the request failed before reaching the server or no response was received.
    // This can happen due to no internet connection, timeout
    data object NetworkError : ApiResult<Nothing>()

    // ParseError: the response was received, but failed to parse into the expected data model.
    // This usually means the JSON format is invalid or unexpected
    data class ParseError(val message: String) : ApiResult<Nothing>()

    // ClientError: an error caused by the app before making the API call
    // For example: invalid QR format, missing data, or no prior QR scan
    data class ClientError(val message: String) : ApiResult<Nothing>()
}
