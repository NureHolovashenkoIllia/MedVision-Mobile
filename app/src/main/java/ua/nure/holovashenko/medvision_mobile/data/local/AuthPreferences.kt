package ua.nure.holovashenko.medvision_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.map
import javax.inject.Inject

val Context.authDataStore by preferencesDataStore(name = "auth_prefs")

class AuthPreferences @Inject constructor(@ApplicationContext context: Context) {

    private val dataStore = context.authDataStore

    private val TOKEN_KEY = stringPreferencesKey("jwt_token")
    private val DOCTOR_ID_KEY = longPreferencesKey("doctor_id")

    val tokenFlow: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }

    suspend fun saveDoctorId(id: Long) {
        dataStore.edit { it[DOCTOR_ID_KEY] = id }
    }

    suspend fun getDoctorId(): Long? {
        return dataStore.data.map { it[DOCTOR_ID_KEY] }.firstOrNull()
    }
}