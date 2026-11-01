package com.easylabour.app.data.repository

import com.easylabour.app.data.api.AuthService
import com.easylabour.app.data.api.BookingService
import com.easylabour.app.data.api.LoginRequest
import com.easylabour.app.data.api.OtpRequest
import com.easylabour.app.data.api.OtpVerificationRequest
import com.easylabour.app.data.api.ReviewService
import com.easylabour.app.data.api.UserService
import com.easylabour.app.data.model.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: AuthService,
    private val sessionManager: SessionManager
) {
    suspend fun sendOtp(mobileNumber: String): String = authService.sendOtp(OtpRequest(mobileNumber))

    suspend fun verifyOtp(mobileNumber: String, otp: String): String = authService.verifyOtp(OtpVerificationRequest(mobileNumber, otp))

    suspend fun login(mobileNumber: String, password: String): AuthResponse {
        val response = authService.login(LoginRequest(mobileNumber, password))
        sessionManager.token = response.token
        return response
    }

    suspend fun register(userDto: UserDto): String = authService.register(userDto)

    fun logout() {
        sessionManager.logout()
    }
}

@Singleton
class BookingRepository @Inject constructor(private val bookingService: BookingService) {
    suspend fun getBookings(userId: String? = null, customerId: String? = null, labourId: String? = null) = 
        bookingService.getBookings(userId, customerId, labourId)
    
    suspend fun createBooking(booking: BookingDto) = bookingService.createBooking(booking)
    
    suspend fun updateBookingStatus(id: String, status: String) = 
        bookingService.updateBookingStatus(id, status)

    suspend fun getBookingHistory(id: String) = bookingService.getBookingHistory(id)
}

@Singleton
class ReviewRepository @Inject constructor(private val reviewService: ReviewService) {
    suspend fun getReviewsByRateeId(rateeId: String) = reviewService.getReviewsByRateeId(rateeId)
    suspend fun createReview(review: ReviewDto) = reviewService.createReview(review)
    suspend fun checkReview(bookingId: String, raterId: String) = reviewService.checkReview(bookingId, raterId)
}

@Singleton
class UserRepository @Inject constructor(val userService: UserService) {
    suspend fun getUser(id: String) = userService.getUser(id)
}
