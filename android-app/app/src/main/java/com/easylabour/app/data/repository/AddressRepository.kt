package com.easylabour.app.data.repository

import com.easylabour.app.data.api.AddressApiService
import com.easylabour.app.data.model.Address
import javax.inject.Inject

class AddressRepository @Inject constructor(
    private val apiService: AddressApiService
) {
    suspend fun getAddresses() = apiService.getAddresses()
    suspend fun createAddress(address: Address) = apiService.createAddress(address)
    suspend fun updateAddress(id: Long, address: Address) = apiService.updateAddress(id, address)
    suspend fun deleteAddress(id: Long) = apiService.deleteAddress(id)
    suspend fun setDefaultAddress(id: Long) = apiService.setDefaultAddress(id)
}
