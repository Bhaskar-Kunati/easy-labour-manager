package com.easylabour.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.viewmodel.BookingViewModel
import com.easylabour.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingsListScreen(
    onBookingSelected: (com.easylabour.app.data.model.BookingDto) -> Unit,
    onNavigateToReview: (String, String, String) -> Unit,
    onNavigateToPayment: (String) -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val bookings by viewModel.bookings.collectAsState()
    val currentUser = viewModel.currentUser

    LaunchedEffect(currentUser) {
        if (currentUser != null) {
            viewModel.fetchBookings(currentUser.id ?: "")
        }
    }

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Upcoming", "Active", "Completed", "Cancelled")

    val filteredBookings = remember(bookings, selectedTabIndex) {
        when (selectedTabIndex) {
            0 -> bookings.filter { it.status == "WAITING" || it.status == "PENDING" }
            1 -> bookings.filter { it.status == "ACCEPTED" || it.status == "IN_PROGRESS" }
            2 -> bookings.filter { it.status == "COMPLETED" }
            3 -> bookings.filter { it.status == "CANCELLED" }
            else -> bookings
        }.sortedBy { it.bookingDate }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Bookings", fontWeight = FontWeight.Bold, color = AlmostBlack) },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = OffWhite
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = PureWhite,
                contentColor = DeepPlum,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = DeepPlum
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { 
                            Text(
                                title, 
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            ) 
                        }
                    )
                }
            }

            LazyColumn(
                contentPadding = PaddingValues(16.dp), 
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredBookings) { booking ->
                    BookingCard(booking, onBookingSelected)
                }
            }
        }
    }
}

@Composable
fun BookingCard(booking: com.easylabour.app.data.model.BookingDto, onClick: (com.easylabour.app.data.model.BookingDto) -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick(booking) },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = LightGray) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = MediumGray)
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(modifier = Modifier.weight(1f)) {
                    Text(booking.serviceType ?: "Service", fontWeight = FontWeight.Bold, color = AlmostBlack)
                    Text(booking.labourName ?: "Unknown", color = DarkGray, fontSize = 14.sp)
                }
                Text(booking.bookingDate?.take(10) ?: "N/A", fontSize = 12.sp, color = DarkGray)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text("Time: ${booking.availabilityWindow ?: "N/A"}", fontSize = 12.sp, color = DarkGray)
            Text("Status: ${booking.status}", fontWeight = FontWeight.Bold, color = DeepPlum, fontSize = 12.sp)
        }
    }
}
