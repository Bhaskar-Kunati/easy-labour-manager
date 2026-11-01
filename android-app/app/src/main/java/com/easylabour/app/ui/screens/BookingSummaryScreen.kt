package com.easylabour.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.data.model.BookingDto
import com.easylabour.app.ui.components.*
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingSummaryScreen(
    booking: BookingDto,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val history by viewModel.bookingHistory.collectAsState()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Service Summary", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(SpacingMedium),
            verticalArrangement = Arrangement.spacedBy(SpacingLarge)
        ) {
            // Status Header Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLarge),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingLarge), horizontalAlignment = Alignment.CenterHorizontally) {
                    val statusColor = when(booking.status) {
                        "COMPLETED" -> Success
                        "CANCELLED" -> Error
                        else -> Primary
                    }
                    StatusBadge(text = booking.status.replace("_", " "), color = statusColor)
                    Spacer(modifier = Modifier.height(SpacingMedium))
                    Text("ID: ${booking.id.take(8).uppercase()}", style = MaterialTheme.typography.labelSmall)
                }
            }

            if (booking.status == "PAYMENT_PENDING") {
                PrimaryButton(
                    text = "PAY NOW",
                    onClick = { onNavigateToPayment(booking.id) }
                )
            }

            // Timeline Section
            Text("Booking Timeline", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusMedium),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingMedium), verticalArrangement = Arrangement.spacedBy(SpacingSmall)) {
                    history.forEach { entry ->
                        Row(verticalAlignment = Alignment.Top) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Surface(modifier = Modifier.size(12.dp), shape = CircleShape, color = Primary) {}
                                Box(modifier = Modifier.width(2.dp).height(30.dp).background(SurfaceVariant))
                            }
                            Spacer(modifier = Modifier.width(SpacingSmall))
                            Column {
                                Text(entry.status, fontWeight = FontWeight.Bold)
                                Text(entry.timestamp, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }
                }
            }

            // Service Details
            Text("Service Insights", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusMedium),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingMedium), verticalArrangement = Arrangement.spacedBy(SpacingMedium)) {
                    DetailItem("Service", booking.serviceType, Icons.Default.Build)
                    DetailItem("Location", booking.location, Icons.Default.LocationOn)
                    DetailItem("Date", booking.bookingDate ?: "Flexible", Icons.Default.Today)
                }
            }

            if (booking.status == "ACCEPTED") {
                PrimaryButton(text = "TRACK SPECIALIST", onClick = { onNavigateToTracking(booking.id) })
            }
        }
    }
}

@Composable
fun DetailItem(label: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = Primary)
        Spacer(Modifier.width(SpacingMedium))
        Column {
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            Text(value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
    }
}
