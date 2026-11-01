package com.easylabour.app.data.api

import com.easylabour.app.data.model.Service
import com.easylabour.app.data.model.ServiceCategory
import retrofit2.Response
import retrofit2.http.*

interface CatalogApiService {
    @GET("categories")
    suspend fun getCategories(): Response<List<ServiceCategory>>

    @GET("services")
    suspend fun getServices(@Query("categoryId") categoryId: Long? = null): Response<List<Service>>

    @GET("services/{id}")
    suspend fun getService(@Path("id") id: Long): Response<Service>

    @GET("services/search")
    suspend fun searchServices(@Query("query") query: String): Response<List<Service>>
}
