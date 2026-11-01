package com.easylabour.app.data.model

import com.google.gson.annotations.SerializedName

data class UserDto(
    @SerializedName("id") val id: String? = null,
    @SerializedName("username") val username: String? = null,
    @SerializedName("email") val email: String,
    @SerializedName("userType") val userType: String,
    @SerializedName("password") val password: String? = null,
    @SerializedName("phoneNumber") val phoneNumber: String? = null,
    @SerializedName("firstName") val firstName: String? = null,
    @SerializedName("lastName") val lastName: String? = null,
    @SerializedName("skills") val skills: Set<String>? = null,
    @SerializedName("averageRating") val averageRating: Double? = null,
    @SerializedName("reviewCount") val reviewCount: Int? = null,
    @SerializedName("completedBookingsCount") val completedBookingsCount: Int? = null
)

data class BookingDto(
    val id: String,
    val customerId: String,
    val customerName: String? = null,
    val labourId: String,
    val labourName: String? = null,
    val serviceType: String,
    val status: String,
    val bookingDate: String? = null,
    val location: String,
    val duration: String? = null,
    val availabilityWindow: String? = null
)

data class AuthResponse(
    val token: String,
    val user: UserDto
)

data class LoginRequest(
    val username: String,
    val password: String
)

data class ReviewDto(
    val id: Long? = null,
    val bookingId: Long,
    val customerId: String,
    val workerId: String,
    val rating: Int,
    val comment: String? = null,
    val createdAt: String? = null
)

data class BookingHistoryDto(
    val id: Long? = null,
    val bookingId: String,
    val status: String,
    val timestamp: String,
    val note: String? = null
)

data class PaymentDto(
    val id: String? = null,
    val bookingId: String,
    val amount: Double,
    val commission: Double? = null,
    val status: String,
    val createdAt: String? = null
)

data class CustomerProfile(
    val id: Long? = null,
    val fullName: String,
    val email: String? = null,
    val gender: String? = null,
    val profileImage: String? = null
)

data class Address(
    val id: Long? = null,
    val addressType: String,
    val fullAddress: String,
    val landmark: String? = null,
    val city: String,
    val state: String,
    val postalCode: String,
    val latitude: Double? = null,
    val longitude: Double? = null,
    val isDefault: Boolean = false
)

data class ServiceCategory(
    val id: Long? = null,
    val name: String,
    val description: String? = null,
    val icon: String? = null,
    val image: String? = null,
    val status: String,
    val displayOrder: Int
)

data class Service(
    val id: Long? = null,
    val categoryId: Long,
    val name: String,
    val description: String? = null,
    val pricingType: String,
    val basePrice: Double,
    val status: String,
    val estimatedDuration: String? = null,
    val image: String? = null
)

data class ScheduledBookingRequest(
    val serviceId: Long,
    val workerCount: Int,
    val scheduledStartTime: String,
    val durationMinutes: Int,
    val address: String,
    val landmark: String?,
    val specialInstructions: String?,
    val couponCode: String?
)

data class OrderRequest(
    val bookingId: Long,
    val customerId: String,
    val amount: Double
)

data class VerifyRequest(
    val paymentId: Long,
    val gatewayPaymentId: String
)

data class SupportTicketDto(
    val id: Long? = null,
    val userId: String,
    val bookingId: Long? = null,
    val subject: String,
    val description: String,
    val category: String,
    val status: String? = null
)
