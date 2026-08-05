package com.calorietracker.di

import android.content.Context
import androidx.work.Configuration
import androidx.work.WorkManager
import androidx.work.WorkRequest
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object WorkManagerModule {

    @Provides
    @Singleton
    fun provideWorkManager(
        @ApplicationContext context: Context,
        workerFactory: HiltWorkerFactory
    ): WorkManager {
        val config = Configuration.Builder()
            .setWorkerFactory(workerFactory)
            .build()
        
        WorkManager.initialize(context, config)
        return WorkManager.getInstance(context)
    }
}

class HiltWorkerFactory @Inject constructor(
    private val workerFactories: Map<Class<out androidx.work.ListenableWorker>, @JvmSuppressWildcards Provider<WorkerFactory>>
) : WorkerFactory() {

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): androidx.work.ListenableWorker? {
        val foundEntry = workerFactories.entries.find { entry ->
            entry.key.name == workerClassName
        }
        
        return foundEntry?.value?.get()?.createWorker(appContext, workerClassName, workerParameters)
    }
}
