package com.easylabour.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easylabour.app.data.model.ReviewDto
import com.easylabour.app.data.repository.ReviewRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val repository: ReviewRepository
) : ViewModel() {

    private val _reviews = MutableStateFlow<List<ReviewDto>>(emptyList())
    val reviews: StateFlow<List<ReviewDto>> = _reviews

    fun fetchReviews(rateeId: String) {
        viewModelScope.launch {
            try {
                _reviews.value = repository.getReviewsByRateeId(rateeId)
            } catch (e: Exception) {
                // Handle error
            }
        }
    }

    suspend fun createReview(review: ReviewDto) {
        repository.createReview(review)
    }

    suspend fun checkReview(bookingId: String, raterId: String): ReviewDto? {
        return try {
            repository.checkReview(bookingId, raterId)
        } catch (e: Exception) {
            null
        }
    }
}
