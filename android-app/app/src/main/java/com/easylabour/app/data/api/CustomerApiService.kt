package com.easylabour.app.data.api

import com.easylabour.app.data.model.CustomerProfile
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.PUT
import retrofit2.http.Body

interface CustomerApiService {
    @GET("customers/me")
    suspend fun getCustomerProfile(): Response<CustomerProfile>

    @PUT("customers/me")
    suspend fun updateCustomerProfile(@Body profile: CustomerProfile): Response<CustomerProfile>
}
