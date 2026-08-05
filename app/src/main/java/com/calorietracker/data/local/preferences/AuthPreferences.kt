package com.calorietracker.data.local.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

val Context.authDataStore: DataStore<Preferences> by preferencesDataStore(name = "auth_preferences")

@Singleton
class AuthPreferences @Inject constructor(
    @ApplicationContext private val context: Context
) {
    companion object {
        val JWT_TOKEN_KEY = stringPreferencesKey("jwt_token")
        val REFRESH_TOKEN_KEY = stringPreferencesKey("refresh_token")
        val USER_ID_KEY = stringPreferencesKey("user_id")
    }

    val jwtTokenFlow: Flow<String?> = context.authDataStore.data
        .map { preferences -> preferences[JWT_TOKEN_KEY] }

    val refreshTokenFlow: Flow<String?> = context.authDataStore.data
        .map { preferences -> preferences[REFRESH_TOKEN_KEY] }

    val userIdFlow: Flow<String?> = context.authDataStore.data
        .map { preferences -> preferences[USER_ID_KEY] }

    suspend fun getJwtToken(): String? {
        return context.authDataStore.data.map { it[JWT_TOKEN_KEY] }.first()
    }

    suspend fun saveAuthData(jwtToken: String, refreshToken: String, userId: String) {
        context.authDataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = jwtToken
            preferences[REFRESH_TOKEN_KEY] = refreshToken
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun saveJwtToken(token: String) {
        context.authDataStore.edit { preferences ->
            preferences[JWT_TOKEN_KEY] = token
        }
    }

    suspend fun saveRefreshToken(token: String) {
        context.authDataStore.edit { preferences ->
            preferences[REFRESH_TOKEN_KEY] = token
        }
    }

    suspend fun saveUserId(userId: String) {
        context.authDataStore.edit { preferences ->
            preferences[USER_ID_KEY] = userId
        }
    }

    suspend fun clearAuthData() {
        context.authDataStore.edit { preferences ->
            preferences.remove(JWT_TOKEN_KEY)
            preferences.remove(REFRESH_TOKEN_KEY)
            preferences.remove(USER_ID_KEY)
        }
    }
}
