package com.easylabour.app.data.repository

import com.easylabour.app.data.api.CustomerApiService
import com.easylabour.app.data.model.CustomerProfile
import javax.inject.Inject

class CustomerRepository @Inject constructor(
    private val apiService: CustomerApiService
) {
    suspend fun getCustomerProfile() = apiService.getCustomerProfile()
    suspend fun updateCustomerProfile(profile: CustomerProfile) = apiService.updateCustomerProfile(profile)
}
