package com.example.securescan.utilities

import com.example.securescan.model.QrConfig
import com.google.gson.Gson

object QrParser {
    private val gson = Gson()

    fun parse(qrContent: String): QrConfig? {
        val trimmed = qrContent.trim()
        if (trimmed.isEmpty()) return null

        // JSON from QR: {"baseUrl":"...","token":"..."} full config in one scan.
        if (!trimmed.startsWith("{")) return null

        return try {
            // convert from json to kotlin object
            val parsed = gson.fromJson(trimmed, QrConfig::class.java)
            if (parsed != null && parsed.baseUrl.isNotBlank() && parsed.token.isNotBlank()) {
                parsed
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}
