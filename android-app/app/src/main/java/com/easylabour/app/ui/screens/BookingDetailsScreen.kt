package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.data.model.BookingDto
import com.easylabour.app.data.model.UserDto
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.BookingViewModel
import kotlinx.coroutines.launch
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingDetailsScreen(
    labourId: String,
    category: String = "General",
    onBookingSuccess: () -> Unit,
    onNavigateBack: () -> Unit,
    onNavigateToHome: () -> Unit,
    viewModel: BookingViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()
    var labour by remember { mutableStateOf<UserDto?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var isBookingInProgress by remember { mutableStateOf(false) }
    var bookingDescription by remember { mutableStateOf("") }
    val durationOptions = listOf("1h", "2h", "3h", "4h", "5h", "6h", "7h", "8h", "Full Day")
    var selectedDuration by remember { mutableStateOf(durationOptions[0]) }
    
    val availabilityOptions = listOf("Early Morning (6 AM - 9 AM)", "Morning (9 AM - 12 PM)", "Afternoon (12 PM - 3 PM)", "Evening (3 PM - 6 PM)", "Night (6 PM - 9 PM)")
    var selectedWindow by remember { mutableStateOf(availabilityOptions[1]) }
    
    var selectedDate by remember { mutableStateOf(java.time.LocalDate.now()) }
    var showDatePicker by remember { mutableStateOf(false) }
    
    var errorMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(labourId) {
        scope.launch {
            try {
                labour = viewModel.getUser(labourId)
            } catch (e: Exception) {
                errorMessage = "Failed to load labour details"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            TopAppBar(
                title = { Text("Confirm Booking", fontWeight = FontWeight.ExtraBold, color = DeepPlum) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeepPlum)
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.Home, contentDescription = "Home", tint = DeepPlum)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = DeepPlum)
            }
        } else if (labour == null || errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(errorMessage ?: "Error loading labour details.")
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Labour Info Header
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(modifier = Modifier.padding(24.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            modifier = Modifier.size(56.dp),
                            shape = CircleShape,
                            color = LightGray
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    Icons.Default.Person, 
                                    contentDescription = null, 
                                    modifier = Modifier.size(36.dp),
                                    tint = MediumGray
                                )
                            }
                        }
                        Spacer(modifier = Modifier.width(20.dp))
                        Column {
                            Text(
                                "${labour?.firstName} ${labour?.lastName}", 
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = AlmostBlack
                            )
                            Text(
                                labour?.skills?.joinToString(", ") ?: "General Labour", 
                                color = DarkGray,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }

                Text(
                    "Booking Details", 
                    style = MaterialTheme.typography.titleLarge, 
                    fontWeight = FontWeight.Bold,
                    color = AlmostBlack
                )

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        OutlinedTextField(
                            value = bookingDescription,
                            onValueChange = { bookingDescription = it },
                            label = { Text("Describe the work needed") },
                            modifier = Modifier.fillMaxWidth().height(120.dp),
                            placeholder = { Text("e.g. Fixing a leaking tap in the kitchen") },
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = DeepPlum)
                        )

                        Text("Select Duration", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = DeepPlum)

                        var expanded by remember { mutableStateOf(false) }

                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedDuration,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Duration") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(focusedBorderColor = DeepPlum),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false }
                            ) {
                                durationOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedDuration = option
                                            expanded = false
                                        }
                                    )
                                }
                            }
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text("Booking Date", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = DeepPlum)
                                Text(selectedDate.toString(), style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = AlmostBlack)
                            }
                            Button(
                                onClick = { showDatePicker = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = BlushPink, contentColor = DeepPlum)
                            ) {
                                Icon(Icons.Default.CalendarToday, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Pick Date")
                            }
                        }

                        Text("Availability Window", style = MaterialTheme.typography.labelLarge, fontWeight = FontWeight.Bold, color = DeepPlum)

                        var windowExpanded by remember { mutableStateOf(false) }
                        ExposedDropdownMenuBox(
                            expanded = windowExpanded,
                            onExpandedChange = { windowExpanded = !windowExpanded },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            OutlinedTextField(
                                value = selectedWindow,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Preferred Time") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = windowExpanded) },
                                colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(focusedBorderColor = DeepPlum),
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )

                            ExposedDropdownMenu(
                                expanded = windowExpanded,
                                onDismissRequest = { windowExpanded = false }
                            ) {
                                availabilityOptions.forEach { option ->
                                    DropdownMenuItem(
                                        text = { Text(option) },
                                        onClick = {
                                            selectedWindow = option
                                            windowExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                if (showDatePicker) {
                    val datePickerState = rememberDatePickerState(
                        initialSelectedDateMillis = selectedDate.atStartOfDay(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli()
                    )
                    DatePickerDialog(
                        onDismissRequest = { showDatePicker = false },
                        confirmButton = {
                            TextButton(onClick = {
                                datePickerState.selectedDateMillis?.let {
                                    selectedDate = java.time.Instant.ofEpochMilli(it)
                                        .atZone(java.time.ZoneId.systemDefault())
                                        .toLocalDate()
                                }
                                showDatePicker = false
                            }) { Text("OK", color = DeepPlum) }
                        },
                        dismissButton = {
                            TextButton(onClick = { showDatePicker = false }) { Text("Cancel", color = DarkGray) }
                        }
                    ) {
                        DatePicker(state = datePickerState, colors = DatePickerDefaults.colors(selectedDayContainerColor = DeepPlum))
                    }
                }

                if (errorMessage != null) {
                    Surface(
                        color = BlushPink,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            errorMessage!!, 
                            color = Red,
                            modifier = Modifier.padding(16.dp),
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                }

                Button(
                    onClick = {
                        isBookingInProgress = true
                        errorMessage = null
                        val newBooking = BookingDto(
                            id = UUID.randomUUID().toString(),
                            customerId = viewModel.currentUser?.id ?: "", 
                            customerName = viewModel.currentUser?.firstName ?: viewModel.currentUser?.username ?: "Unknown",
                            labourId = labourId,
                            labourName = "${labour?.firstName} ${labour?.lastName}",
                            serviceType = category,
                            status = "WAITING",
                            bookingDate = selectedDate.atStartOfDay().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")),
                            location = "Current Location",
                            duration = selectedDuration,
                            availabilityWindow = selectedWindow
                        )
                        scope.launch {
                            try {
                                if (newBooking.customerId.isEmpty()) {
                                    errorMessage = "User session expired. Please login again."
                                } else {
                                    viewModel.createBooking(newBooking)
                                    onBookingSuccess()
                                }
                            } catch (e: Exception) {
                                errorMessage = "Failed to create booking: ${e.message}"
                            } finally {
                                isBookingInProgress = false
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = DeepPlum, contentColor = PureWhite),
                    enabled = !isBookingInProgress
                ) {
                    if (isBookingInProgress) {
                        CircularProgressIndicator(modifier = Modifier.size(24.dp), color = PureWhite)
                    } else {
                        Text("Confirm & Book Now", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }

                OutlinedButton(
                    onClick = onNavigateToHome,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !isBookingInProgress,
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = DarkGray),
                    border = androidx.compose.foundation.BorderStroke(1.dp, LightGrayText)
                ) {
                    Text("Cancel & Return Home", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(40.dp))
            }
        }
    }
}
