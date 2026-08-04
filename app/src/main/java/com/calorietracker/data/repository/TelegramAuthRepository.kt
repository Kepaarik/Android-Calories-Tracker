package com.calorietracker.data.repository

import android.util.Log
import com.calorietracker.data.local.TelegramUserDao
import com.calorietracker.data.model.TelegramUser
import kotlinx.coroutines.flow.Flow
import org.json.JSONObject
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TelegramAuthRepository @Inject constructor(
    private val telegramUserDao: TelegramUserDao
) {

    companion object {
        private const val BOT_TOKEN = "YOUR_BOT_TOKEN" // Replace with actual bot token from Telegram Bot Father
    }

    fun getCurrentUser(): Flow<TelegramUser?> = 
        telegramUserDao.getCurrentUser()

    suspend fun getCurrentUserSync(): TelegramUser? = 
        telegramUserDao.getCurrentUserSync()

    suspend fun saveUser(user: TelegramUser) {
        telegramUserDao.insertUser(user)
    }

    suspend fun deleteUser() {
        telegramUserDao.deleteAllUsers()
    }

    /**
     * Validates Telegram Web App auth data
     * @param initData The init_data string from Telegram Web App
     * @return true if valid, false otherwise
     */
    fun validateTelegramAuth(initData: String): Boolean {
        return try {
            val params = parseInitData(initData)
            val hash = params["hash"] ?: return false
            
            val dataCheckString = params
                .filterKeys { it != "hash" }
                .toSortedMap()
                .map { "${it.key}=${it.value}" }
                .joinToString("\n")

            val secretKey = hmacSha256("WebAppData", BOT_TOKEN)
            val calculatedHash = hmacSha256(dataCheckString, secretKey).toHexString()

            calculatedHash == hash
        } catch (e: Exception) {
            Log.e("TelegramAuth", "Validation error", e)
            false
        }
    }

    /**
     * Parses Telegram user data from init_data
     */
    fun parseTelegramUser(initData: String): TelegramUser? {
        return try {
            val params = parseInitData(initData)
            val userJson = params["user"] ?: return null
            val userObj = JSONObject(userJson)

            TelegramUser(
                id = userObj.getLong("id"),
                firstName = userObj.getString("first_name"),
                lastName = userObj.optString("last_name", null),
                username = userObj.optString("username", null),
                photoUrl = userObj.optJSONObject("photo_url")?.optString("small") 
                    ?: userObj.optJSONObject("photo_url")?.optString("large"),
                authDate = userObj.getLong("auth_date"),
                hash = params["hash"] ?: ""
            )
        } catch (e: Exception) {
            Log.e("TelegramAuth", "Parse user error", e)
            null
        }
    }

    private fun parseInitData(initData: String): Map<String, String> {
        return initData.split("&")
            .associate { 
                val (key, value) = it.split("=", limit = 2)
                key to java.net.URLDecoder.decode(value, "UTF-8")
            }
    }

    private fun hmacSha256(message: String, key: String): ByteArray {
        val mac = javax.crypto.Mac.getInstance("HmacSHA256")
        mac.init(javax.crypto.spec.SecretKeySpec(key.toByteArray(), "HmacSHA256"))
        return mac.doFinal(message.toByteArray())
    }

    private fun ByteArray.toHexString(): String {
        return joinToString("") { "%02x".format(it) }
    }
}
