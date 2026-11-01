package com.easylabour.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easylabour.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AllServicesScreen(
    onNavigateBack: () -> Unit,
    onServiceSelected: (String) -> Unit
) {
    val services = listOf(
        "Plumbing" to Icons.Default.WaterDrop,
        "Electrician" to Icons.Default.Bolt,
        "Carpentry" to Icons.Default.Construction,
        "Painting" to Icons.Default.FormatPaint,
        "Cleaning" to Icons.Default.CleaningServices,
        "AC Repair" to Icons.Default.AcUnit,
        "Appliance" to Icons.Default.Kitchen,
        "Computer" to Icons.Default.Computer,
        "Moving" to Icons.Default.LocalShipping,
        "Pest" to Icons.Default.BugReport
    )

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("All Services", fontWeight = FontWeight.ExtraBold, color = DeepPlum) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeepPlum)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = PureWhite)
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(services) { service ->
                Card(
                    modifier = Modifier
                        .height(100.dp)
                        .clickable { onServiceSelected(service.first) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = PureWhite),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(service.second, contentDescription = null, tint = DeepPlum, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(service.first, fontSize = 12.sp, fontWeight = FontWeight.Medium, color = AlmostBlack)
                    }
                }
            }
        }
    }
}
