package com.easylabour.app.data.api

import com.easylabour.app.data.model.Address
import retrofit2.Response
import retrofit2.http.*

interface AddressApiService {
    @GET("addresses")
    suspend fun getAddresses(): Response<List<Address>>

    @POST("addresses")
    suspend fun createAddress(@Body address: Address): Response<Address>

    @PUT("addresses/{id}")
    suspend fun updateAddress(@Path("id") id: Long, @Body address: Address): Response<Address>

    @DELETE("addresses/{id}")
    suspend fun deleteAddress(@Path("id") id: Long): Response<Void>

    @PUT("addresses/{id}/default")
    suspend fun setDefaultAddress(@Path("id") id: Long): Response<Void>
}
