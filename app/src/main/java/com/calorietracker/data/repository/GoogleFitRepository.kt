package com.calorietracker.data.repository

import android.content.Context
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.tasks.Tasks
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleFitRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val fitnessApi = Fitness.getHistoryClient(context, null)
    
    private val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestScopes(Fitness.SCOPE_ACTIVITY_READ_WRITE)
        .build()

    suspend fun getTodaySteps(): Int {
        return try {
            val request = DataReadRequest.Builder()
                .read(DataType.TYPE_STEP_COUNT_DELTA)
                .setTimeRange(
                    getStartOfDayMillis(),
                    System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build()

            val response = Tasks.await(fitnessApi.readData(request))
            
            val totalSteps = response.getDataSet(DataType.TYPE_STEP_COUNT_DELTA)
                .dataPoints
                .sumOf { it.getValue(DataType.FIELD_STEPS).asInt() }
            
            totalSteps
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error getting steps", e)
            0
        }
    }

    suspend fun getTodayCaloriesBurned(): Int {
        return try {
            val request = DataReadRequest.Builder()
                .read(DataType.TYPE_CALORIES_EXPENDED)
                .setTimeRange(
                    getStartOfDayMillis(),
                    System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build()

            val response = Tasks.await(fitnessApi.readData(request))
            
            val totalCalories = response.getDataSet(DataType.TYPE_CALORIES_EXPENDED)
                .dataPoints
                .sumOf { it.getValue(DataType.FIELD_CALORIES).asFloat().toInt() }
            
            totalCalories
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error getting calories", e)
            0
        }
    }

    suspend fun getTodayDistance(): Float {
        return try {
            val request = DataReadRequest.Builder()
                .read(DataType.TYPE_DISTANCE_DELTA)
                .setTimeRange(
                    getStartOfDayMillis(),
                    System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build()

            val response = Tasks.await(fitnessApi.readData(request))
            
            val totalDistance = response.getDataSet(DataType.TYPE_DISTANCE_DELTA)
                .dataPoints
                .sumOf { it.getValue(DataType.FIELD_DISTANCE).asFloat() }
            
            totalDistance
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error getting distance", e)
            0f
        }
    }

    suspend fun getTodayActiveMinutes(): Int {
        return try {
            val request = DataReadRequest.Builder()
                .read(DataType.TYPE_DURATION)
                .setTimeRange(
                    getStartOfDayMillis(),
                    System.currentTimeMillis(),
                    TimeUnit.MILLISECONDS
                )
                .build()

            val response = Tasks.await(fitnessApi.readData(request))
            
            val totalMinutes = response.getDataSet(DataType.TYPE_DURATION)
                .dataPoints
                .sumOf { it.getValue(DataType.FIELD_DURATION).asInt(TimeUnit.MINUTES) }
            
            totalMinutes
        } catch (e: Exception) {
            Log.e("GoogleFit", "Error getting active minutes", e)
            0
        }
    }

    private fun getStartOfDayMillis(): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getGoogleSignInOptions(): GoogleSignInOptions = gso
}
