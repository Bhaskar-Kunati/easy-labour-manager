package com.easylabour.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easylabour.app.data.model.BookingDto
import com.easylabour.app.data.model.UserDto
import com.easylabour.app.data.repository.BookingRepository
import com.easylabour.app.data.repository.SessionManager
import com.easylabour.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookingViewModel @Inject constructor(
    private val repository: BookingRepository,
    private val userRepository: UserRepository,
    private val sessionManager: SessionManager
) : ViewModel() {

    val currentUser: UserDto? get() = sessionManager.currentUser

    private val _bookings = MutableStateFlow<List<BookingDto>>(emptyList())
    val bookings: StateFlow<List<BookingDto>> = _bookings

    private val _selectedBooking = MutableStateFlow<BookingDto?>(null)
    val selectedBooking: StateFlow<BookingDto?> = _selectedBooking

    private val _bookingHistory = MutableStateFlow<List<com.easylabour.app.data.model.BookingHistoryDto>>(emptyList())
    val bookingHistory: StateFlow<List<com.easylabour.app.data.model.BookingHistoryDto>> = _bookingHistory

    fun selectBooking(booking: BookingDto) {
        _selectedBooking.value = booking
        fetchBookingHistory(booking.id)
    }

    fun fetchBookingHistory(bookingId: String) {
        viewModelScope.launch {
            try {
                _bookingHistory.value = repository.getBookingHistory(bookingId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    fun fetchBookings(userId: String, isLabour: Boolean = false) {
        viewModelScope.launch {
            try {
                _bookings.value = if (isLabour) {
                    repository.getBookings(labourId = userId)
                } else {
                    repository.getBookings(customerId = userId)
                }
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun getUser(id: String): UserDto {
        return userRepository.getUser(id)
    }

    suspend fun createBooking(booking: BookingDto) {
        repository.createBooking(booking)
    }

    suspend fun updateBookingStatus(bookingId: String, status: String) {
        repository.updateBookingStatus(bookingId, status)
        // Refresh bookings after update
        val isLabour = currentUser?.userType == "LABOUR"
        fetchBookings(currentUser?.id ?: "", isLabour)
    }
}
