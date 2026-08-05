package com.calorietracker.data.remote.interceptor

import okhttp3.Interceptor
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor

object InterceptorFactory {

    fun createAuthInterceptor(tokenProvider: () -> String?): AuthInterceptor {
        return AuthInterceptor(tokenProvider)
    }

    fun createLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }
    }
}
