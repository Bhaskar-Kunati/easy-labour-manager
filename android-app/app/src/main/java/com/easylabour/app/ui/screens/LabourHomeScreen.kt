package com.easylabour.app.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.ui.components.*
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.BookingViewModel
import com.easylabour.app.viewmodel.LabourUiState
import com.easylabour.app.viewmodel.LabourViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabourHomeScreen(
    onNavigateToReview: (String, String, String) -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel(),
    labourViewModel: LabourViewModel = hiltViewModel()
) {
    val bookings by bookingViewModel.bookings.collectAsState()
    val uiState by labourViewModel.uiState.collectAsState()
    val currentUser = labourViewModel.currentUser
    val scope = rememberCoroutineScope()
    
    val allCategories = listOf(
        ServiceCategory("Plumbing", Icons.Default.WaterDrop),
        ServiceCategory("Electric", Icons.Default.ElectricBolt),
        ServiceCategory("Cleaning", Icons.Default.CleaningServices),
        ServiceCategory("Carpentry", Icons.Default.Handyman),
        ServiceCategory("Painting", Icons.Default.FormatPaint),
        ServiceCategory("Masonry", Icons.Default.Foundation)
    )

    var selectedSkills by remember { 
        mutableStateOf(currentUser?.skills ?: emptySet()) 
    }

    Box(modifier = Modifier.fillMaxSize().background(Background)) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Command Center", fontWeight = FontWeight.Bold, color = TextPrimary) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Surface)
                )
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = SpacingMedium)
            ) {
                // Today's Summary Card
                item {
                    Spacer(modifier = Modifier.height(SpacingMedium))
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusLarge),
                        colors = CardDefaults.cardColors(containerColor = Secondary),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(SpacingLarge)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(modifier = Modifier.size(48.dp), shape = CircleShape, color = Accent.copy(alpha = 0.2f)) {
                                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Dashboard, contentDescription = null, tint = Accent) }
                                }
                                Spacer(modifier = Modifier.width(SpacingMedium))
                                Text("Today's Summary", color = Surface, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            }
                            
                            Spacer(modifier = Modifier.height(SpacingLarge))
                            
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                SummaryStat(label = "Pending", value = bookings.count { it.status == "WAITING" }.toString(), color = Accent)
                                SummaryStat(label = "Active", value = bookings.count { it.status == "ACCEPTED" || it.status == "WORK_IN_PROGRESS" }.toString(), color = PrimaryContainer)
                                SummaryStat(label = "Done", value = bookings.count { it.status == "WORK_DONE" || it.status.startsWith("PAYMENT") || it.status == "COMPLETED" }.toString(), color = Success)
                            }
                        }
                    }
                }

                // Expertise Section
                item {
                    Spacer(modifier = Modifier.height(SpacingLarge))
                    Text("My Expertise", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(SpacingMedium))
                    
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Surface),
                        shape = RoundedCornerShape(RadiusMedium),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                    ) {
                        Column(modifier = Modifier.padding(SpacingMedium)) {
                            val rows = allCategories.chunked(3)
                            rows.forEach { rowItems ->
                                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                                    rowItems.forEach { cat ->
                                        val isSelected = selectedSkills.contains(cat.name)
                                        LabourPremiumCategoryItem(cat, isSelected) {
                                            selectedSkills = if (isSelected) selectedSkills - cat.name else selectedSkills + cat.name
                                        }
                                    }
                                }
                                Spacer(modifier = Modifier.height(SpacingSmall))
                            }
                            Spacer(modifier = Modifier.height(SpacingMedium))
                            PrimaryButton(
                                text = "UPDATE SKILLS",
                                onClick = { labourViewModel.updateSkills(selectedSkills) },
                                isLoading = uiState is LabourUiState.Loading
                            )
                        }
                    }
                }

                // Job Requests
                item {
                    Spacer(modifier = Modifier.height(SpacingLarge))
                    Text("Incoming Requests", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Spacer(modifier = Modifier.height(SpacingSmall))
                }

                if (bookings.isEmpty()) {
                    item {
                        Text("No incoming tasks", color = TextSecondary, modifier = Modifier.padding(SpacingLarge))
                    }
                } else {
                    items(bookings) { booking ->
                        PremiumJobCard(
                            booking = booking,
                            onStatusUpdate = { status -> scope.launch { bookingViewModel.updateBookingStatus(booking.id, status) } },
                            onRateCustomer = { onNavigateToReview(booking.id, booking.labourId, booking.customerId) }
                        )
                    }
                }
                item { Spacer(modifier = Modifier.height(SpacingExtraLarge)) }
            }
        }
    }
}

@Composable
fun SummaryStat(label: String, value: String, color: Color) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, color = color, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
        Text(label, color = Surface.copy(alpha = 0.7f), style = MaterialTheme.typography.labelSmall)
    }
}

@Composable
fun LabourPremiumCategoryItem(category: ServiceCategory, isSelected: Boolean, onClick: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
        Surface(
            modifier = Modifier.size(56.dp),
            onClick = onClick,
            shape = RoundedCornerShape(RadiusMedium),
            color = if (isSelected) PrimaryContainer else SurfaceVariant,
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(category.icon, contentDescription = category.name, modifier = Modifier.size(24.dp), tint = if (isSelected) Primary else TextSecondary)
            }
        }
        Text(category.name, style = MaterialTheme.typography.labelSmall, modifier = Modifier.padding(top = SpacingSmall), color = if (isSelected) Primary else TextSecondary)
    }
}

@Composable
fun PremiumJobCard(booking: com.easylabour.app.data.model.BookingDto, onStatusUpdate: (String) -> Unit, onRateCustomer: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(vertical = SpacingSmall),
        shape = RoundedCornerShape(RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Column(modifier = Modifier.padding(SpacingMedium)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(color = PrimaryContainer, shape = RoundedCornerShape(RadiusSmall), modifier = Modifier.size(40.dp)) {
                    Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Build, contentDescription = null, tint = Primary) }
                }
                Spacer(modifier = Modifier.width(SpacingMedium))
                Column {
                    Text(booking.serviceType, fontWeight = FontWeight.Bold, color = TextPrimary)
                    Text("Customer: ${booking.customerName}", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                }
            }
            Spacer(modifier = Modifier.height(SpacingMedium))
            
            if (booking.status == "WAITING") {
                Row(horizontalArrangement = Arrangement.spacedBy(SpacingSmall)) {
                    Button(onClick = { onStatusUpdate("ACCEPTED") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(RadiusSmall), colors = ButtonDefaults.buttonColors(containerColor = Success)) { Text("Accept") }
                    OutlinedButton(onClick = { onStatusUpdate("REJECTED") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(RadiusSmall), colors = ButtonDefaults.outlinedButtonColors(contentColor = Error)) { Text("Reject") }
                }
            } else {
                StatusBadge(text = booking.status.replace("_", " "), color = Primary)
            }
        }
    }
}
