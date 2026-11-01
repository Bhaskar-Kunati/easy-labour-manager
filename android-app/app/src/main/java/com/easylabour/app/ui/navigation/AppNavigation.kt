package com.easylabour.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.easylabour.app.ui.screens.*

@Composable
fun AppNavigation(navController: NavHostController) {
    NavHost(navController = navController, startDestination = "role_selection") {
        composable("role_selection") {
            RoleSelectionScreen(
                onNavigateToCustomerRegister = { navController.navigate("register/CUSTOMER") },
                onNavigateToProfessionalRegister = { navController.navigate("register/LABOUR") }
            )
        }
        composable("login") {
            LoginScreen(
                onLoginSuccess = { user ->
                    navController.navigate("main/${user.userType}/${user.username}/${user.email}") {
                        popUpTo("login") { inclusive = true }
                    }
                },
                onNavigateToRegister = { navController.navigate("role_selection") },
                onNavigateToDiagnostics = { navController.navigate("network_diagnostic") }
            )
        }
        composable(
            "register/{userType}",
            arguments = listOf(navArgument("userType") { defaultValue = "CUSTOMER" })
        ) { backStackEntry ->
            val userType = backStackEntry.arguments?.getString("userType") ?: "CUSTOMER"
            RegisterScreen(
                userType = userType,
                onNavigateToLogin = { navController.navigate("login") },
                onRegisterSuccess = {
                    navController.navigate("otp_verification/placeholder_number")
                }
            )
        }
        composable(
            "otp_verification/{mobileNumber}",
            arguments = listOf(navArgument("mobileNumber") { defaultValue = "" })
        ) { backStackEntry ->
            val mobileNumber = backStackEntry.arguments?.getString("mobileNumber") ?: ""
            OtpVerificationScreen(
                mobileNumber = mobileNumber,
                onNavigateBack = { navController.popBackStack() },
                onOtpVerified = {
                    navController.navigate("login") {
                        popUpTo("register") { inclusive = true }
                    }
                }
            )
        }
        composable(
            "main/{role}/{username}/{email}",
            arguments = listOf(
                navArgument("role") { defaultValue = "CUSTOMER" },
                navArgument("username") { defaultValue = "" },
                navArgument("email") { defaultValue = "" }
            )
        ) { backStackEntry ->
            val role = backStackEntry.arguments?.getString("role") ?: "CUSTOMER"
            val username = backStackEntry.arguments?.getString("username") ?: ""
            val email = backStackEntry.arguments?.getString("email") ?: ""
            
            MainScreen(
                userRole = role,
                username = username,
                email = email,
                onLogout = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                },
                onNavigateToTracking = { bookingId ->
                    navController.navigate("tracking/$bookingId")
                },
                onNavigateToPayment = { bookingId ->
                    navController.navigate("payment/$bookingId")
                }
            )
        }
        composable(
            "tracking/{bookingId}",
            arguments = listOf(navArgument("bookingId") { defaultValue = "" })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            TrackingScreen(bookingId = bookingId)
        }
        composable(
            "payment/{bookingId}",
            arguments = listOf(navArgument("bookingId") { defaultValue = "" })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            PaymentScreen(
                bookingId = bookingId,
                onPaymentSuccess = { bid, raterId, rateeId ->
                    navController.navigate("review/$bid/$raterId/$rateeId") {
                        popUpTo("payment/$bid") { inclusive = true }
                    }
                },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("network_diagnostic") {
            NetworkDiagnosticScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(
            "history/{bookingId}",
            arguments = listOf(navArgument("bookingId") { defaultValue = "" })
        ) { backStackEntry ->
            val bookingId = backStackEntry.arguments?.getString("bookingId") ?: ""
            BookingHistoryScreen(
                bookingId = bookingId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
