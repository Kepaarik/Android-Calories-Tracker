package com.calorietracker.data.remote.interceptor

import com.calorietracker.data.local.preferences.AuthPreferences
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthInterceptor @Inject constructor(
    private val authPreferences: AuthPreferences
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // Skip authentication for non-API requests or public endpoints
        val url = originalRequest.url.toString()
        if (url.contains("/auth/login") || 
            url.contains("/auth/register") || 
            url.contains("/auth/telegram")) {
            return chain.proceed(originalRequest)
        }

        // Get JWT token from preferences
        val token = runBlocking {
            authPreferences.getJwtToken()
        }

        // If no token, proceed without it (server will return 401)
        if (token == null) {
            return chain.proceed(originalRequest)
        }

        // Add Authorization header
        val authenticatedRequest = originalRequest.newBuilder()
            .header("Authorization", "Bearer $token")
            .build()

        return chain.proceed(authenticatedRequest)
    }
}
