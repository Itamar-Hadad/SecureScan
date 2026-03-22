package com.example.securescan.utilities

import com.example.securescan.model.QrConfig
import com.google.gson.Gson

object QrParser {
    private val gson = Gson()

    fun parse(qrContent: String): QrConfig? {
        return try {
            // convert from json to kotlin object
            gson.fromJson(qrContent, QrConfig::class.java)
        } catch (e: Exception) {
            null
        }
    }
}