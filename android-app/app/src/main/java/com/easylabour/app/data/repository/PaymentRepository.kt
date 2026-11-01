package com.easylabour.app.data.repository

import com.easylabour.app.data.api.PaymentService
import com.easylabour.app.data.model.OrderRequest
import com.easylabour.app.data.model.VerifyRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PaymentRepository @Inject constructor(private val paymentService: PaymentService) {
    suspend fun createOrder(request: OrderRequest) = paymentService.createOrder(request)
    suspend fun verifyPayment(request: VerifyRequest) = paymentService.verifyPayment(request)
}
