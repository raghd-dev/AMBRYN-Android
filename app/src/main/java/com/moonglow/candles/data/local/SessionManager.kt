package com.moonglow.candles.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.moonglow.candles.data.model.UserProfile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore(name = "session")

class SessionManager(
    private val context: Context
) {
    private val nameKey = stringPreferencesKey("user_name")
    private val emailKey = stringPreferencesKey("user_email")

    val userFlow: Flow<UserProfile?> =
        context.dataStore.data.map { prefs ->
            val name = prefs[nameKey]
            val email = prefs[emailKey]

            if (name != null && email != null) {
                UserProfile(
                    name = name,
                    email = email
                )
            } else {
                null
            }
        }

    suspend fun saveUser(
        name: String,
        email: String
    ) {
        context.dataStore.edit { prefs ->
            prefs[nameKey] = name
            prefs[emailKey] = email
        }
    }

    suspend fun clearUser() {
        context.dataStore.edit { prefs ->
            prefs.clear()
        }
    }
}