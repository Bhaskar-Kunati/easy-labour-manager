package com.easylabour.app.data.api

import com.easylabour.app.data.model.*
import retrofit2.http.*

interface AuthService {
    @POST("api/v1/auth/send-otp")
    suspend fun sendOtp(@Body request: OtpRequest): String

    @POST("api/v1/auth/verify-otp")
    suspend fun verifyOtp(@Body request: OtpVerificationRequest): String

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @POST("api/v1/auth/register")
    suspend fun register(@Body request: UserDto): String
}

data class OtpRequest(val mobileNumber: String)
data class OtpVerificationRequest(val mobileNumber: String, val otp: String)
data class LoginRequest(val mobileNumber: String, val password: String)

interface BookingService {
    @GET("bookings")
    suspend fun getBookings(
        @Query("userId") userId: String? = null,
        @Query("customerId") customerId: String? = null,
        @Query("labourId") labourId: String? = null
    ): List<BookingDto>

    @POST("bookings")
    suspend fun createBooking(@Body booking: BookingDto): BookingDto

    @POST("api/v1/bookings/scheduled")
    suspend fun createScheduledBooking(@Body request: ScheduledBookingRequest): BookingDto

    @GET("api/v1/bookings/upcoming")
    suspend fun getUpcomingBookings(): List<BookingDto>

    @PATCH("bookings/{id}/status")
    suspend fun updateBookingStatus(@Path("id") id: String, @Query("status") status: String): BookingDto

    @GET("bookings/{id}/history")
    suspend fun getBookingHistory(@Path("id") id: String): List<BookingHistoryDto>
}

interface ReviewService {
    // Phase 10 - New review endpoints
    @POST("api/v1/reviews")
    suspend fun createReview(@Body review: ReviewDto): ReviewDto

    @GET("api/v1/reviews/worker/{workerId}")
    suspend fun getReviewsByWorker(@Path("workerId") workerId: String): List<ReviewDto>
    
    // Existing Review methods
    @GET("reviews/ratee/{rateeId}")
    suspend fun getReviewsByRateeId(@Path("rateeId") rateeId: String): List<ReviewDto>

    @POST("reviews")
    suspend fun createReviewOld(@Body review: ReviewDto): ReviewDto

    @GET("reviews/check")
    suspend fun checkReview(
        @Query("bookingId") bookingId: String,
        @Query("raterId") raterId: String
    ): ReviewDto?
}

interface UserService {
    @POST("users")
    suspend fun createUser(@Body user: UserDto): UserDto

    @GET("users/{id}")
    suspend fun getUser(@Path("id") id: String): UserDto

    @GET("users/search")
    suspend fun searchBySkill(@Query("skill") skill: String): List<UserDto>

    @PUT("users/{id}/skills")
    suspend fun updateSkills(@Path("id") id: String, @Body skills: Set<String>): UserDto
}

interface PaymentService {
    @POST("api/v1/payments/create-order")
    suspend fun createOrder(@Body request: OrderRequest): Payment

    @POST("api/v1/payments/verify")
    suspend fun verifyPayment(@Body request: VerifyRequest): Boolean
}

interface SupportService {
    @POST("api/v1/support/tickets")
    suspend fun createTicket(@Body ticket: SupportTicketDto): SupportTicketDto

    @GET("api/v1/support/tickets")
    suspend fun getTicketsByUser(@Query("userId") userId: String): List<SupportTicketDto>
}

data class Payment(
    val id: Long,
    val bookingId: Long,
    val amount: Double,
    val paymentStatus: String
)

interface NotificationService {
    @GET("api/notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): List<NotificationDto>
}

data class NotificationDto(
    val id: String?,
    val userId: String?,
    val message: String?,
    val type: String?,
    val read: Boolean?
)
