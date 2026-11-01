package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.viewmodel.TrackingViewModel
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrackingScreen(
    bookingId: String,
    viewModel: TrackingViewModel = hiltViewModel()
) {
    val location by viewModel.locationFlow.collectAsState()
    
    // Default location or last known location
    val labourLocation = location?.let { LatLng(it.latitude, it.longitude) } ?: LatLng(1.35, 103.87)

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(labourLocation, 15f)
    }

    // Update camera when location changes
    LaunchedEffect(location) {
        if (location != null) {
            cameraPositionState.position = CameraPosition.fromLatLngZoom(labourLocation, 15f)
        }
    }

    LaunchedEffect(bookingId) {
        viewModel.startTracking(bookingId)
    }

    Scaffold(
        topBar = { TopAppBar(title = { Text("Tracking Booking #$bookingId") }) }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState
            ) {
                Marker(
                    state = MarkerState(position = labourLocation),
                    title = "Labour Location",
                    snippet = "Live location"
                )
            }
        }
    }
}
