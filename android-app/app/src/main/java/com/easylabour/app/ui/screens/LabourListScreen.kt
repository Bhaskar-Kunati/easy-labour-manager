package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.data.api.UserService
import com.easylabour.app.data.model.UserDto
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.LabourListViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabourListScreen(
    category: String,
    onNavigateBack: () -> Unit,
    onLabourSelected: (String) -> Unit,
    viewModel: LabourListViewModel = hiltViewModel()
) {
    val scope = rememberCoroutineScope()
    var labourers by remember { mutableStateOf<List<UserDto>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    val userService = viewModel.userService
    var sortBy by remember { mutableStateOf("Rating") }

    LaunchedEffect(category, sortBy) {
        scope.launch {
            try {
                // Use capitalized category name to match backend data
                val results = if (category.equals("All", ignoreCase = true)) {
                    userService.searchBySkill("")
                } else {
                    val formattedCategory = category.lowercase().capitalize(java.util.Locale.ROOT)
                    userService.searchBySkill(formattedCategory)
                }
                
                labourers = when (sortBy) {
                    "Rating" -> results.sortedByDescending { it.averageRating ?: 0.0 }
                    "History" -> results.sortedByDescending { it.completedBookingsCount ?: 0 }
                    else -> results
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    Scaffold(
        containerColor = OffWhite,
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        "$category Specialists", 
                        fontWeight = FontWeight.ExtraBold,
                        color = DeepPlum
                    ) 
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = DeepPlum)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = PureWhite
                ),
                actions = {
                    var showMenu by remember { mutableStateOf(false) }
                    IconButton(onClick = { showMenu = true }) {
                        Icon(Icons.Default.Star, contentDescription = "Sort", tint = DeepPlum)
                    }
                    DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                        DropdownMenuItem(
                            text = { Text("Sort by Rating") },
                            onClick = { sortBy = "Rating"; showMenu = false }
                        )
                        DropdownMenuItem(
                            text = { Text("Sort by History") },
                            onClick = { sortBy = "History"; showMenu = false }
                        )
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (labourers.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Surface(
                    color = PureWhite,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        "No $category specialists found.", 
                        modifier = Modifier.padding(24.dp),
                        color = DarkGray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                item { Spacer(modifier = Modifier.height(16.dp)) }
                items(labourers) { labour ->
                    LabourCard(labour, onLabourSelected)
                }
                item { Spacer(modifier = Modifier.height(32.dp)) }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LabourCard(labour: UserDto, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        onClick = { onClick(labour.id ?: "") },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = CircleShape,
                color = LightGray
            ) {
                Box(contentAlignment = Alignment.Center) { 
                    Icon(Icons.Default.Person, contentDescription = null, tint = MediumGray) 
                }
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                val fullName = if (!labour.firstName.isNullOrEmpty() || !labour.lastName.isNullOrEmpty()) {
                    "${labour.firstName ?: ""} ${labour.lastName ?: ""}".trim()
                } else {
                    labour.username ?: "Unknown Labour"
                }
                
                Text(
                    text = fullName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = AlmostBlack
                )
                
                Text(
                    text = labour.skills?.joinToString(", ") ?: "General Labour",
                    style = MaterialTheme.typography.bodySmall,
                    color = DarkGray
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = Amber
                    )
                    Text(
                        text = "${labour.averageRating ?: 0.0} (${labour.reviewCount ?: 0})",
                        style = MaterialTheme.typography.bodySmall,
                        color = AlmostBlack
                    )
                }
            }
            
            Button(
                onClick = { onClick(labour.id ?: "") },
                colors = ButtonDefaults.buttonColors(containerColor = BlushPink, contentColor = DeepPlum),
                shape = RoundedCornerShape(8.dp)
            ) { 
                Text("Book Now", fontWeight = FontWeight.Bold) 
            }
        }
    }
}
