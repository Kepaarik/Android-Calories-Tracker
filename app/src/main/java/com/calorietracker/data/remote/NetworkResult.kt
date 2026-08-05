package com.calorietracker.data.remote

sealed class NetworkResult<out T> {
    data class Success<T>(val data: T) : NetworkResult<T>()
    data class Error(val code: Int, val message: String) : NetworkResult<Nothing>()
    object Loading : NetworkResult<Nothing>()
}

suspend inline fun <T> safeApiCall(
    crossinline apiCall: suspend () -> retrofit2.Response<T>
): NetworkResult<T> {
    return try {
        val response = apiCall()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                NetworkResult.Success(body)
            } else {
                NetworkResult.Error(response.code(), "Empty response body")
            }
        } else {
            val errorMessage = response.errorBody()?.string() ?: "Unknown error"
            NetworkResult.Error(response.code(), errorMessage)
        }
    } catch (e: Exception) {
        NetworkResult.Error(-1, e.message ?: "Network request failed")
    }
}
