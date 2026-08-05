package com.calorietracker.util

object Constants {
    // API
    const val BASE_URL = "http://localhost:8001"
    const val API_TIMEOUT_SECONDS = 30L

    // Database
    const val DATABASE_NAME = "calorie_tracker_db"

    // DataStore
    const val SETTINGS_DATASTORE_NAME = "settings"
    const val JWT_TOKEN_KEY = "jwt_token"
    const val USER_ID_KEY = "user_id"
    const val THEME_KEY = "theme"

    // Theme
    const val THEME_LIGHT = "light"
    const val THEME_DARK = "dark"
    const val THEME_SYSTEM = "system"

    // Date format
    const val DATE_FORMAT_ISO_8601 = "yyyy-MM-dd"
    const val DATE_FORMAT_DISPLAY = "dd.MM.yyyy"

    // Water intake
    const val DEFAULT_WATER_GOAL_ML = 2000
    const val WATER_STEP_ML = 250

    // Pagination
    const val PAGE_SIZE = 20

    // Weight
    const val MIN_WEIGHT_KG = 30.0
    const val MAX_WEIGHT_KG = 300.0

    // Product search
    const val MIN_SEARCH_QUERY_LENGTH = 2
}
