package ua.nure.holovashenko.medvision_mobile.util

import android.util.Base64
import android.util.Log
import ua.nure.holovashenko.medvision_mobile.domain.model.UserRole

fun parseRoleFromToken(token: String): Pair<UserRole?, Long?> {
    val TAG = "JwtParser"

    return try {
        val parts = token.split(".")
        if (parts.size != 3) {
            Log.w(TAG, "Token does not have 3 parts: $token")
            return Pair(null, null)
        }

        val payload = parts[1]
        val decodedBytes = Base64.decode(payload, Base64.URL_SAFE or Base64.NO_WRAP)
        val json = String(decodedBytes, Charsets.UTF_8)

        Log.d(TAG, "Decoded JWT payload: $json")

        val roleRegex = Regex("\"role\"\\s*:\\s*\"(\\w+)\"")
        val idRegex = Regex("\"userId\"\\s*:\\s*(\\d+)")

        val role = roleRegex.find(json)?.groups?.get(1)?.value?.let { UserRole.fromString(it) }
        val userId = idRegex.find(json)?.groups?.get(1)?.value?.toLongOrNull()

        Log.d(TAG, "Extracted role string: ${role.toString()}")
        Log.d(TAG, "Extracted userId string: ${userId.toString()}")

        Pair(role, userId)
    } catch (e: Exception) {
        Log.e(TAG, "Failed to parse role from token", e)
        Pair(null, null)
    }
}