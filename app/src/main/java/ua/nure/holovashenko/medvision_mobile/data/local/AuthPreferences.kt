package ua.nure.holovashenko.medvision_mobile.data.local

import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton
import androidx.core.content.edit

@Singleton
class AuthPreferences @Inject constructor(
    private val prefs: SharedPreferences
) {
    companion object {
        private const val TOKEN_KEY = "jwt_token"
        private const val DOCTOR_ID_KEY = "doctor_id"
    }

    fun saveToken(token: String) {
        prefs.edit { putString(TOKEN_KEY, token) }
    }

    fun getToken(): String? {
        return prefs.getString(TOKEN_KEY, null)
    }

    fun clearToken() {
        prefs.edit { remove(TOKEN_KEY) }
    }

    fun saveDoctorId(id: Long) {
        prefs.edit { putLong(DOCTOR_ID_KEY, id) }
    }

    fun getDoctorId(): Long? {
        val id = prefs.getLong(DOCTOR_ID_KEY, -1L)
        return if (id != -1L) id else null
    }

    fun clearAll() {
        prefs.edit { clear() }
    }
}