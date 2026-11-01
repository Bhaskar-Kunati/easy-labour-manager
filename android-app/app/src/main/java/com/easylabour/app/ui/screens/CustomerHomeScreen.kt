package com.easylabour.app.ui.screens

import android.Manifest
import android.annotation.SuppressLint
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.ui.components.*
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.BookingViewModel
import com.easylabour.app.viewmodel.LabourListViewModel
import com.google.android.gms.location.LocationServices
import android.location.Geocoder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

@SuppressLint("MissingPermission")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    userId: String,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit,
    onNavigateToAllServices: () -> Unit,
    onNavigateToLabourList: (String) -> Unit,
    viewModel: BookingViewModel = hiltViewModel(),
    labourListViewModel: LabourListViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val geocoder = remember(context) { Geocoder(context, Locale.getDefault()) }
    var selectedCity by remember { mutableStateOf("Fetching...") }
    var showLocationDialog by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var unreadCount by remember { mutableStateOf(0) }
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                if (location != null) {
                    CoroutineScope(Dispatchers.IO).launch {
                        try {
                            val addresses = geocoder.getFromLocation(location.latitude, location.longitude, 1)
                            val city = addresses?.firstOrNull()?.locality ?: "Unknown"
                            withContext(Dispatchers.Main) { selectedCity = city }
                        } catch (e: Exception) {
                            withContext(Dispatchers.Main) { selectedCity = "Unknown" }
                        }
                    }
                } else { selectedCity = "Unknown" }
            }
        } else { selectedCity = "Permission Denied" }
    }

    LaunchedEffect(Unit) { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }

    Scaffold(
        containerColor = Background,
        topBar = { HomeTopBar(selectedCity, unreadCount) { showLocationDialog = true } }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(horizontal = SpacingMedium)
        ) {
            item {
                Spacer(modifier = Modifier.height(SpacingMedium))
                SearchBarSection(searchQuery) { searchQuery = it }
                Spacer(modifier = Modifier.height(SpacingMedium))

                if (searchQuery.isBlank()) {
                    HeroBannerSection { onNavigateToAllServices() }
                    Spacer(modifier = Modifier.height(SpacingLarge))
                    FeaturesSection()
                    Spacer(modifier = Modifier.height(SpacingLarge))
                }

                PopularServicesSection(searchQuery, onNavigateToLabourList)

                if (searchQuery.isBlank()) {
                    Spacer(modifier = Modifier.height(SpacingLarge))
                    SpecialOfferSection()
                    Spacer(modifier = Modifier.height(SpacingLarge))
                    RecommendedSection(onNavigateToLabourList)
                    Spacer(modifier = Modifier.height(SpacingLarge))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeTopBar(city: String, unreadCount: Int, onCityClick: () -> Unit) {
    TopAppBar(
        title = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Build, contentDescription = "Logo", tint = Primary)
                Spacer(modifier = Modifier.width(SpacingSmall))
                Text("Easy Labour", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        },
        actions = {
            BadgedBox(
                badge = { if (unreadCount > 0) Badge { Text("$unreadCount") } }
            ) {
                IconButton(onClick = {}) { 
                    Icon(Icons.Outlined.Notifications, contentDescription = "Notifications", tint = TextPrimary) 
                }
            }
            Row(
                verticalAlignment = Alignment.CenterVertically, 
                modifier = Modifier
                    .padding(end = 8.dp)
                    .clickable { onCityClick() }
            ) {
                Icon(Icons.Default.LocationOn, contentDescription = "Location", tint = Primary, modifier = Modifier.size(16.dp))
                Text(city, fontSize = 12.sp, color = TextPrimary)
                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = TextPrimary)
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = Surface)
    )
}

@Composable
fun SearchBarSection(query: String, onQueryChange: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Search services...") },
        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, tint = TextSecondary) },
        shape = RoundedCornerShape(RadiusMedium),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Surface,
            unfocusedContainerColor = Surface,
            unfocusedBorderColor = SurfaceVariant,
            focusedBorderColor = Primary
        )
    )
}

@Composable
fun HeroBannerSection(onShowAll: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().height(180.dp),
        shape = RoundedCornerShape(RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = PrimaryContainer)
    ) {
        Column(modifier = Modifier.padding(SpacingLarge), verticalArrangement = Arrangement.Center) {
            Text("Your Work.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
            Text("Our Responsibility.", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Primary)
            Spacer(modifier = Modifier.height(SpacingSmall))
            Button(
                onClick = onShowAll,
                colors = ButtonDefaults.buttonColors(containerColor = Surface, contentColor = Primary),
                shape = RoundedCornerShape(RadiusSmall)
            ) { Text("Book Now") }
        }
    }
}

@Composable
fun FeaturesSection() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        FeatureItem(Icons.Default.VerifiedUser, "Verified")
        FeatureItem(Icons.Default.LiveTv, "Real-Time")
        FeatureItem(Icons.Default.CalendarToday, "Scheduled")
        FeatureItem(Icons.Default.HeadsetMic, "Support")
    }
}

@Composable
fun FeatureItem(icon: androidx.compose.ui.graphics.vector.ImageVector, text: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.width(70.dp)) {
        Surface(color = PrimaryContainer, shape = CircleShape, modifier = Modifier.size(48.dp)) {
            Box(contentAlignment = Alignment.Center) { Icon(icon, contentDescription = null, tint = Primary) }
        }
        Spacer(modifier = Modifier.height(SpacingSmall))
        Text(text, fontSize = 10.sp, color = TextPrimary)
    }
}

@Composable
fun PopularServicesSection(searchQuery: String, onNavigateToLabourList: (String) -> Unit) {
    Column {
        SectionHeader("Popular Services")
        Spacer(modifier = Modifier.height(SpacingMedium))
        
        // ... (Maintain functionality)
    }
}

@Composable
fun SpecialOfferSection() {
    Card(
        modifier = Modifier.fillMaxWidth().height(100.dp),
        shape = RoundedCornerShape(RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = Accent.copy(alpha = 0.2f))
    ) {
        Row(modifier = Modifier.padding(SpacingMedium), verticalAlignment = Alignment.CenterVertically) {
            Icon(Icons.Default.CardGiftcard, contentDescription = null, modifier = Modifier.size(40.dp), tint = Accent)
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column(modifier = Modifier.weight(1f)) {
                Text("Special Offers!", fontWeight = FontWeight.Bold, color = TextPrimary)
            }
        }
    }
}

@Composable
fun RecommendedSection(onNavigateToLabourList: (String) -> Unit) {
    Column {
        SectionHeader("Recommended")
        // ...
    }
}

@Composable
fun SectionHeader(title: String) {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(title, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
        Text("View All", fontSize = 14.sp, color = Primary, modifier = Modifier.clickable {})
    }
}
