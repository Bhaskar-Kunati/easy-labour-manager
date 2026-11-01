package com.easylabour.app.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.easylabour.app.data.model.LocationUpdate
import com.easylabour.app.data.remote.TrackingWebSocketService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackingViewModel @Inject constructor(
    private val trackingService: TrackingWebSocketService
) : ViewModel() {

    val locationFlow: StateFlow<LocationUpdate?> = trackingService.locationFlow

    fun startTracking(bookingId: String) {
        trackingService.connect(bookingId)
    }

    override fun onCleared() {
        super.onCleared()
        trackingService.disconnect()
    }
}
