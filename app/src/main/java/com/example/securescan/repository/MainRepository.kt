package com.example.securescan.repository

import com.example.securescan.interfaces.ApiService
import com.example.securescan.model.ApiResult
import com.example.securescan.model.AuthRequest
import com.example.securescan.model.AuthResponse
import com.example.securescan.model.UserResponse
import com.example.securescan.utilities.QrParser
import com.example.securescan.utilities.RetrofitClient
import com.google.gson.JsonParser
import java.io.IOException

// class that handles the communication with the server, the view model talk to this class and not directly to Retrofit
object MainRepository {

    //var that save the ApiService after we scan the qr and create the api
    private var apiService: ApiService? = null


    // clear the api session
    fun clearApiSession() {
        apiService = null
    }

    suspend fun resolveQrData(rawText: String): ApiResult<UserResponse> {
        // parse the QR content into a QrConfig object
        val config = QrParser.parse(rawText)
            ?: return ApiResult.ClientError("Invalid QR format")

        //create Retrofit with the Url from the qr and save it in the global var (apiService)
        val newService = try {
            RetrofitClient.createService(config.baseUrl)
        } catch (e: IllegalArgumentException) {
            return ApiResult.ClientError("Invalid base URL: ${e.message}")
        }
        this.apiService = newService

        return try {
            //send the token to the server and get the user info
            val response = newService.resolveQr(mapOf("qr_token" to config.token))
            val body = response.body()

            if (response.isSuccessful && body != null) {
                ApiResult.Success(body)
            } else {
                val errorText = response.errorBody()?.use { it.string() }
                ApiResult.HttpError(
                    //code is the status code from the server 400, 200
                    code = response.code(),
                    message = messageFromServerErrorBody(errorText) ?: response.message()
                )
            }
        } catch (e: IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.ParseError(e.message ?: e.toString())
        }
    }


    suspend fun authenticateUser(userId: String, password: String): ApiResult<AuthResponse> {
        //check if there is an active connection
        val currentService = apiService
            ?: return ApiResult.ClientError("No active connection. Please scan QR first.")

        return try {
            //send the password to the server and get the response
            val response = currentService.validatePassword(AuthRequest(userId, password))
            val body = response.body()

            if (response.isSuccessful && body != null) {
                when {
                    //if in the response the user is authenticated we return success
                    body.authenticated == true -> ApiResult.Success(body)
                    else -> {
                        //try to find the error message in the respons, the first one
                        val msg = listOfNotNull(body.error, body.message)
                            .firstOrNull { !it.isNullOrBlank() }
                            ?: "Authentication failed"
                        ApiResult.ClientError(msg)
                    }
                }
            } else {
                //if the response is not successful we return the error
                val errorText = response.errorBody()?.use { it.string() }
                ApiResult.HttpError(
                    code = response.code(),
                    message = messageFromServerErrorBody(errorText) ?: response.message()
                )
            }
        } catch (e: IOException) {
            ApiResult.NetworkError
        } catch (e: Exception) {
            ApiResult.ParseError(e.message ?: e.toString())
        }
    }


    //function that help us to get the error message from the server
    //the error comes in the response body in json format { "detail": "error message" }
    private fun messageFromServerErrorBody(raw: String?): String? {
        //trim the response body
        val text = raw?.trim().orEmpty()
        if (text.isEmpty()) return null
        return try {
            //convert the json to a json object
            val jsonObject = JsonParser.parseString(text).asJsonObject
            //get the error message from the json object
            val detail = jsonObject.get("detail") ?: return text
            when {
                //if the error message is a string we return it
                detail.isJsonPrimitive && detail.asJsonPrimitive.isString -> detail.asString
                //if the error message is an object we try to get the error message from it
                detail.isJsonObject -> {
                    val obj = detail.asJsonObject
                    //check if there is an error message in the object
                    obj.get("error")?.asString?.takeIf { it.isNotBlank() }
                        ?: obj.get("message")?.asString?.takeIf { it.isNotBlank() }
                }
                else -> null
            }?.takeIf { it.isNotBlank() } ?: text
        } catch (_: Exception) {
            text
        }
    }
}
