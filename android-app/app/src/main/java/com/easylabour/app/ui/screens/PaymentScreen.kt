package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.ui.components.*
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.BookingViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PaymentScreen(
    bookingId: String,
    onPaymentSuccess: (String, String, String) -> Unit,
    onNavigateBack: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var isProcessing by remember { mutableStateOf(false) }
    val bookings by viewModel.bookings.collectAsState()
    val booking = bookings.find { it.id == bookingId }
    
    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = { Text("Complete Payment", fontWeight = FontWeight.Bold) },
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
                .padding(SpacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(SpacingExtraLarge))
            
            Surface(
                modifier = Modifier.size(100.dp),
                shape = CircleShape,
                color = PrimaryContainer
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(Icons.Default.Payments, contentDescription = null, modifier = Modifier.size(48.dp), tint = Primary)
                }
            }
            
            Spacer(modifier = Modifier.height(SpacingLarge))
            
            Text("Payment for Service", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("Booking ID: #${bookingId.take(8).uppercase()}", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            
            Spacer(modifier = Modifier.height(SpacingExtraLarge))
            
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusMedium),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingLarge)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Service Amount", color = TextSecondary)
                        Text("$100.00", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Platform Fee", color = TextSecondary)
                        Text("$5.00", fontWeight = FontWeight.Bold)
                    }
                    Divider(modifier = Modifier.padding(vertical = SpacingMedium), color = SurfaceVariant)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Total Amount", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold)
                        Text("$105.00", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Primary)
                    }
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            PrimaryButton(
                text = "PAY NOW",
                onClick = { 
                    isProcessing = true
                    scope.launch {
                        try {
                            viewModel.updateBookingStatus(bookingId, "PAYMENT_DONE")
                            val raterId = booking?.customerId ?: ""
                            val rateeId = booking?.labourId ?: ""
                            onPaymentSuccess(bookingId, raterId, rateeId)
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isProcessing = false
                        }
                    }
                },
                isLoading = isProcessing
            )
            
            Spacer(modifier = Modifier.height(SpacingMedium))
            Text("Secure Encrypted Payment", style = MaterialTheme.typography.labelSmall, color = TextSecondary)
        }
    }
}
