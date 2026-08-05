package com.calorietracker.data.repository

import com.calorietracker.data.local.dao.UserDao
import com.calorietracker.data.mapper.UserMapper.toDomain
import com.calorietracker.data.mapper.UserMapper.toEntity
import com.calorietracker.data.remote.api.UserProfileApi
import com.calorietracker.domain.model.User
import com.calorietracker.domain.repository.UserProfileRepository

class UserProfileRepositoryImpl(
    private val userDao: UserDao,
    private val userProfileApi: UserProfileApi
) : UserProfileRepository {

    override suspend fun getUserProfile(userId: Int): Result<User> {
        return try {
            // Сначала пробуем получить из локальной базы
            val localUser = userDao.getUserById(userId)?.toDomain()
            
            if (localUser != null) {
                // Обновляем данные с сервера в фоне
                try {
                    val response = userProfileApi.getUserProfile(userId)
                    if (response.isSuccessful && response.body() != null) {
                        val dto = response.body()!!
                        userDao.insertUser(dto.toEntity())
                        return Result.success(dto.toDomain())
                    }
                } catch (apiException: Exception) {
                    // Игнорируем ошибки сети, возвращаем локальные данные
                }
                
                Result.success(localUser)
            } else {
                // Если нет локально, получаем с сервера
                val response = userProfileApi.getUserProfile(userId)
                if (response.isSuccessful && response.body() != null) {
                    val dto = response.body()!!
                    userDao.insertUser(dto.toEntity())
                    Result.success(dto.toDomain())
                } else {
                    Result.failure(Exception("Failed to get user profile: ${response.code()}"))
                }
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateUserProfile(user: User): Result<User> {
        return try {
            val entity = user.toEntity()
            userDao.insertUser(entity)
            
            // Отправляем обновленные данные на сервер
            val response = userProfileApi.updateUserProfile(user.id, user.toDto())
            if (response.isSuccessful && response.body() != null) {
                val updatedDto = response.body()!!
                userDao.insertUser(updatedDto.toEntity())
                Result.success(updatedDto.toDomain())
            } else {
                // Возвращаем локально обновленного пользователя
                Result.success(user)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
