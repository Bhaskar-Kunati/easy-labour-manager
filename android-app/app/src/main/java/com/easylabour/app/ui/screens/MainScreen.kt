package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubbleOutline
import androidx.compose.material.icons.filled.PersonOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.*
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.viewmodel.BookingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    userRole: String,
    username: String,
    email: String,
    onLogout: () -> Unit,
    onNavigateToTracking: (String) -> Unit,
    onNavigateToPayment: (String) -> Unit,
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination

    val items = listOf("Home", "Bookings", "Messages", "Profile")

    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEach { item ->
                    val route = item.lowercase()
                    val selected = currentDestination?.hierarchy?.any { it.route == route } == true
                    
                    NavigationBarItem(
                        icon = {
                            when (item) {
                                "Home" -> Icon(Icons.Default.Home, contentDescription = item)
                                "Bookings" -> Icon(Icons.Default.History, contentDescription = item)
                                "Messages" -> Icon(Icons.Default.ChatBubbleOutline, contentDescription = item)
                                "Profile" -> Icon(Icons.Default.PersonOutline, contentDescription = item)
                            }
                        },
                        label = { Text(item) },
                        selected = selected,
                        onClick = {
                            navController.navigate(route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                if (userRole == "CUSTOMER") {
                    CustomerHomeScreen(
                        userId = username,
                        onNavigateToTracking = onNavigateToTracking,
                        onNavigateToPayment = onNavigateToPayment,
                        onNavigateToAllServices = { navController.navigate("all_services") },
                        onNavigateToLabourList = { category ->
                            navController.navigate("labour_list/$category")
                        }
                    )
                } else {
                    LabourHomeScreen(
                        onNavigateToReview = { bookingId, raterId, rateeId ->
                            navController.navigate("review/$bookingId/$raterId/$rateeId")
                        }
                    )
                }
            }
            composable("all_services") {
                AllServicesScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onServiceSelected = { category ->
                        navController.navigate("labour_list/$category")
                    }
                )
            }
            composable("labour_list/{category}") { backStackEntry ->
                val category = backStackEntry.arguments?.getString("category") ?: ""
                LabourListScreen(
                    category = category,
                    onNavigateBack = { navController.popBackStack() },
                    onLabourSelected = { labourId ->
                        navController.navigate("booking_details/$labourId/$category")
                    }
                )
            }
            composable("booking_details/{labourId}/{category}") { backStackEntry ->
                val labourId = backStackEntry.arguments?.getString("labourId") ?: ""
                val category = backStackEntry.arguments?.getString("category") ?: ""
                BookingDetailsScreen(
                    labourId = labourId,
                    category = category,
                    onBookingSuccess = {
                        navController.navigate("bookings") {
                            popUpTo("home")
                        }
                    },
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToHome = {
                        navController.navigate("home") {
                            popUpTo("home") { inclusive = true }
                        }
                    }
                )
            }
            composable("bookings") { 
                BookingsListScreen(
                    onBookingSelected = { booking ->
                        bookingViewModel.selectBooking(booking)
                        navController.navigate("booking_summary")
                    },
                    onNavigateToReview = { bookingId, raterId, rateeId ->
                        navController.navigate("review/$bookingId/$raterId/$rateeId")
                    },
                    onNavigateToPayment = onNavigateToPayment
                ) 
            }
            composable("messages") {
                MessagesScreen(onNavigateBack = { navController.popBackStack() })
            }
            composable("review/{bookingId}/{raterId}/{rateeId}") { backStackEntry ->
                val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
                val raterId = backStackEntry.arguments?.getString("raterId") ?: ""
                val rateeId = backStackEntry.arguments?.getString("rateeId") ?: ""
                ReviewScreen(
                    bookingId = bookingId,
                    raterId = raterId,
                    rateeId = rateeId,
                    onReviewSubmitted = { navController.popBackStack() },
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable("booking_summary") {
                val selectedBooking by bookingViewModel.selectedBooking.collectAsState()
                selectedBooking?.let { booking ->
                    BookingSummaryScreen(
                        booking = booking,
                        onNavigateToPayment = onNavigateToPayment,
                        onNavigateToTracking = onNavigateToTracking,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }
            }
            composable("profile") {
                ProfileScreen(
                    username = username,
                    email = email,
                    userType = userRole,
                    onLogout = onLogout
                )
            }
            composable("help") { HelpScreen() }
        }
    }
}
