package com.easylabour.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.R
import com.easylabour.app.data.model.UserDto
import com.easylabour.app.ui.components.PrimaryButton
import com.easylabour.app.ui.theme.*
import com.easylabour.app.viewmodel.AuthState
import com.easylabour.app.viewmodel.AuthViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    userType: String,
    onNavigateToLogin: () -> Unit,
    onRegisterSuccess: () -> Unit,
    viewModel: AuthViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var skills by remember { mutableStateOf("") }

    val authState by viewModel.authState.collectAsState()

    LaunchedEffect(authState) {
        if (authState is AuthState.Success) {
            onRegisterSuccess()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bg_auth),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(SpacingMedium)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(SpacingExtraLarge))
            
            Surface(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(RadiusLarge),
                color = Surface.copy(alpha = 0.95f),
                tonalElevation = 8.dp
            ) {
                Image(
                    painter = painterResource(id = R.drawable.easy_labour_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.padding(SpacingSmall),
                    contentScale = ContentScale.Fit
                )
            }

            Spacer(modifier = Modifier.height(SpacingMedium))
            Text(
                "Create $userType Account",
                style = MaterialTheme.typography.headlineMedium,
                color = Surface,
                fontWeight = FontWeight.ExtraBold
            )

            Spacer(modifier = Modifier.height(SpacingLarge))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(RadiusLarge),
                colors = CardDefaults.cardColors(containerColor = Surface)
            ) {
                Column(modifier = Modifier.padding(SpacingLarge)) {
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Username") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    OutlinedTextField(
                        value = email,
                        onValueChange = { email = it },
                        label = { Text("Email Address") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium),
                        leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(SpacingSmall)) {
                        OutlinedTextField(
                            value = firstName,
                            onValueChange = { firstName = it },
                            label = { Text("First Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(RadiusMedium)
                        )
                        OutlinedTextField(
                            value = lastName,
                            onValueChange = { lastName = it },
                            label = { Text("Last Name") },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(RadiusMedium)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    OutlinedTextField(
                        value = phoneNumber,
                        onValueChange = { phoneNumber = it },
                        label = { Text("Mobile Number") },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) }
                    )
                    Spacer(modifier = Modifier.height(SpacingSmall))
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Create Password") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(RadiusMedium),
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) }
                    )
                    
                    if (userType == "LABOUR") {
                        Spacer(modifier = Modifier.height(SpacingSmall))
                        OutlinedTextField(
                            value = skills,
                            onValueChange = { skills = it },
                            label = { Text("My Skills (comma separated)") },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(RadiusMedium),
                            leadingIcon = { Icon(Icons.Default.Build, contentDescription = null) }
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(SpacingLarge))

                    PrimaryButton(
                        text = "CREATE ACCOUNT",
                        onClick = {
                            val skillsSet = if (userType == "LABOUR") {
                                skills.split(",").map { it.trim() }.filter { it.isNotEmpty() }.toSet()
                            } else null

                            val user = UserDto(
                                username = username,
                                email = email,
                                firstName = firstName,
                                lastName = lastName,
                                phoneNumber = phoneNumber,
                                userType = userType,
                                password = password,
                                skills = skillsSet
                            )
                            viewModel.register(user)
                        },
                        isLoading = authState is AuthState.Loading
                    )
                }
            }

            Spacer(modifier = Modifier.height(SpacingMedium))
            TextButton(onClick = onNavigateToLogin) {
                Text("Already have an account? Login", color = Surface)
            }
            Spacer(modifier = Modifier.height(SpacingExtraLarge))
        }
    }
}
