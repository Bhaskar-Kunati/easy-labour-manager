package com.easylabour.app.data.repository

import com.easylabour.app.data.api.CatalogApiService
import javax.inject.Inject

class CatalogRepository @Inject constructor(
    private val apiService: CatalogApiService
) {
    suspend fun getCategories() = apiService.getCategories()
    suspend fun getServices(categoryId: Long? = null) = apiService.getServices(categoryId)
    suspend fun getService(id: Long) = apiService.getService(id)
    suspend fun searchServices(query: String) = apiService.searchServices(query)
}
