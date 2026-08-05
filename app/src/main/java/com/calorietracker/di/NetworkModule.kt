package com.calorietracker.di

import android.content.Context
import com.calorietracker.data.remote.interceptor.AuthInterceptor
import com.calorietracker.data.remote.interceptor.InterceptorFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun provideMoshi(): Moshi = Moshi.Builder()
        .add(KotlinJsonAdapterFactory())
        .build()

    @Provides
    @Singleton
    @Named("BaseUrl")
    fun provideBaseUrl(): String = "http://localhost:8001"

    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: AuthInterceptor
    ): OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(authInterceptor)
        .addInterceptor(InterceptorFactory.createLoggingInterceptor())
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    @Provides
    @Singleton
    fun provideAuthInterceptor(
        @ApplicationContext context: Context
    ): AuthInterceptor {
        val tokenProvider = {
            context.getSharedPreferences("calorie_tracker_prefs", Context.MODE_PRIVATE)
                .getString("auth_token", null)
        }
        return InterceptorFactory.createAuthInterceptor(tokenProvider)
    }

    @Provides
    @Singleton
    fun provideRetrofit(
        @Named("BaseUrl") baseUrl: String,
        moshi: Moshi,
        okHttpClient: OkHttpClient
    ): Retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(MoshiConverterFactory.create(moshi))
        .client(okHttpClient)
        .build()

    @Provides
    @Singleton
    fun provideAuthApi(retrofit: Retrofit): com.calorietracker.data.remote.api.AuthApi =
        retrofit.create(com.calorietracker.data.remote.api.AuthApi::class.java)

    @Provides
    @Singleton
    fun provideDiaryApi(retrofit: Retrofit): com.calorietracker.data.remote.api.DiaryApi =
        retrofit.create(com.calorietracker.data.remote.api.DiaryApi::class.java)

    @Provides
    @Singleton
    fun provideProductApi(retrofit: Retrofit): com.calorietracker.data.remote.api.ProductApi =
        retrofit.create(com.calorietracker.data.remote.api.ProductApi::class.java)

    @Provides
    @Singleton
    fun provideUserProfileApi(retrofit: Retrofit): com.calorietracker.data.remote.api.UserProfileApi =
        retrofit.create(com.calorietracker.data.remote.api.UserProfileApi::class.java)

    @Provides
    @Singleton
    fun provideWeightApi(retrofit: Retrofit): com.calorietracker.data.remote.api.WeightApi =
        retrofit.create(com.calorietracker.data.remote.api.WeightApi::class.java)
}
