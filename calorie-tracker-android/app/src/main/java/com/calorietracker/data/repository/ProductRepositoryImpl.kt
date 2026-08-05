package com.calorietracker.data.repository

import com.calorietracker.data.local.dao.ProductDao
import com.calorietracker.data.mapper.ProductMapper.toDomain
import com.calorietracker.data.mapper.ProductMapper.toEntity
import com.calorietracker.domain.model.Product
import com.calorietracker.domain.repository.ProductRepository

class ProductRepositoryImpl(
    private val productDao: ProductDao,
    private val productApi: com.calorietracker.data.remote.api.ProductApi
) : ProductRepository {

    override suspend fun searchProducts(query: String): List<Product> {
        return try {
            // Сначала ищем в локальной базе
            val localProducts = productDao.searchProducts(query).map { it.toDomain() }
            
            if (localProducts.isNotEmpty()) {
                localProducts
            } else {
                // Если не найдено локально, делаем запрос к API
                val response = productApi.searchProducts(query)
                if (response.isSuccessful && response.body() != null) {
                    val dtoList = response.body()!!
                    // Сохраняем в локальную базу
                    productDao.insertProducts(dtoList.map { it.toEntity() })
                    dtoList.map { it.toDomain() }
                } else {
                    emptyList()
                }
            }
        } catch (e: Exception) {
            // При ошибке сети возвращаем только локальные данные
            productDao.searchProducts(query).map { it.toDomain() }
        }
    }

    override suspend fun getProductById(id: Int): Product? {
        // Сначала пробуем получить из локальной базы
        val localProduct = productDao.getProductById(id)?.toDomain()
        
        if (localProduct != null) {
            return localProduct
        }
        
        // Если не найдено локально, пробуем получить из API
        return try {
            val response = productApi.getProductById(id)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                productDao.insertProduct(dto.toEntity())
                dto.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun getProductByBarcode(barcode: String): Product? {
        // Сначала пробуем получить из локальной базы
        val localProduct = productDao.getProductByBarcode(barcode)?.toDomain()
        
        if (localProduct != null) {
            return localProduct
        }
        
        // Если не найдено локально, пробуем получить из API
        return try {
            val response = productApi.getProductByBarcode(barcode)
            if (response.isSuccessful && response.body() != null) {
                val dto = response.body()!!
                productDao.insertProduct(dto.toEntity())
                dto.toDomain()
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addProduct(product: Product): Result<Product> {
        return try {
            val entity = product.toEntity()
            val insertedId = productDao.insertProduct(entity)
            
            val insertedProduct = product.copy(id = insertedId.toInt())
            
            // Также отправляем на сервер
            try {
                val response = productApi.addProduct(insertedProduct.toDto())
                if (response.isSuccessful && response.body() != null) {
                    val serverProduct = response.body()!!.toDomain()
                    productDao.insertProduct(serverProduct.toEntity())
                    Result.success(serverProduct)
                } else {
                    Result.success(insertedProduct)
                }
            } catch (apiException: Exception) {
                // Если ошибка сети, возвращаем локальный продукт
                Result.success(insertedProduct)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateProduct(product: Product): Result<Unit> {
        return try {
            val entity = product.toEntity()
            productDao.updateProduct(entity)
            
            // Также обновляем на сервере
            try {
                val response = productApi.updateProduct(product.id, product.toDto())
                if (!response.isSuccessful) {
                    // Логируем ошибку, но не прерываем выполнение
                }
            } catch (apiException: Exception) {
                // Игнорируем ошибки сети для обновления
            }
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
