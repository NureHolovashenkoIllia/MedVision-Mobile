package ua.nure.holovashenko.medvision_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthPreferences @Inject constructor(@ApplicationContext context: Context) {
    private val Context.dataStore by preferencesDataStore("auth_prefs")

    private val dataStore = context.dataStore
    private val TOKEN_KEY = stringPreferencesKey("jwt_token")

    val tokenFlow: Flow<String?> = dataStore.data.map { it[TOKEN_KEY] }

    suspend fun saveToken(token: String) {
        dataStore.edit { it[TOKEN_KEY] = token }
    }

    suspend fun clearToken() {
        dataStore.edit { it.remove(TOKEN_KEY) }
    }
}
