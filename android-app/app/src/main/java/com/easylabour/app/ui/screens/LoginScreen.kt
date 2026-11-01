package com.easylabour.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.R
import com.easylabour.app.ui.components.PrimaryButton
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.AuthState
import com.easylabour.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    onLoginSuccess: (com.easylabour.app.data.model.UserDto) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToDiagnostics: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val authState by viewModel.authState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg_auth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Diagnostic Settings Button
        IconButton(
            onClick = onNavigateToDiagnostics,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(16.dp)
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_preferences),
                contentDescription = "Network Settings",
                tint = Color.White
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo
            Surface(
                modifier = Modifier.size(160.dp), // Increased size
                shape = RoundedCornerShape(32.dp),
                color = Color.White.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.easy_labour_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(4.dp), // Reduced padding for 'zoom' effect
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
            Text(
                "Welcome To EasyLabour",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontSize = 28.sp, // Slightly smaller to fit in one line
                    letterSpacing = 0.sp
                ),
                color = Color.White,
                fontWeight = FontWeight.ExtraBold, // More modern bold
                maxLines = 1
            )
            Text(
                "Sign in to continue",
                style = MaterialTheme.typography.bodyLarge,
                color = Color.White.copy(alpha = 0.8f)
            )

            Spacer(modifier = Modifier.height(40.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLarge),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingLarge)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium)
                    )
                    Spacer(modifier = Modifier.height(SpacingMedium))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium)
                    )
                    Spacer(modifier = Modifier.height(SpacingLarge))

                    PrimaryButton(
                        text = "LOGIN",
                        onClick = { viewModel.login(username, password) },
                        isLoading = authState is AuthState.Loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            TextButton(onClick = onNavigateToRegister) {
                Text("Don't have an account? Register", color = Color.White)
            }

            if (authState is AuthState.Success) {
                LaunchedEffect(Unit) {
                    onLoginSuccess((authState as AuthState.Success).response.user)
                }
            }

            if (authState is AuthState.Error) {
                Text(
                    (authState as AuthState.Error).message,
                    color = MaterialTheme.colorScheme.errorContainer,
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}
