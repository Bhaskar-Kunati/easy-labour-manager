package com.easylabour.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.easylabour.app.ui.theme.*

@Composable
fun RoleSelectionScreen(
    onNavigateToCustomerRegister: () -> Unit,
    onNavigateToProfessionalRegister: () -> Unit
) {
    Scaffold(containerColor = Background) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(SpacingLarge),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Welcome to Easy Labour", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold, color = TextPrimary)
            Spacer(modifier = Modifier.height(SpacingSmall))
            Text("Choose how you want to use Easy Labour", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
            Spacer(modifier = Modifier.height(SpacingExtraLarge))

            RoleCard(
                title = "Find a Professional",
                description = "Book trusted professionals for your home, construction, and business needs.",
                icon = Icons.Default.Person,
                onClick = onNavigateToCustomerRegister
            )
            Spacer(modifier = Modifier.height(SpacingMedium))
            RoleCard(
                title = "Join as a Professional",
                description = "Find work opportunities and grow your professional career.",
                icon = Icons.Default.Work,
                onClick = onNavigateToProfessionalRegister
            )
        }
    }
}

@Composable
fun RoleCard(title: String, description: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth().clickable { onClick() },
        shape = RoundedCornerShape(RadiusMedium),
        colors = CardDefaults.cardColors(containerColor = Surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(modifier = Modifier.padding(SpacingLarge), verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = null, modifier = Modifier.size(40.dp), tint = Primary)
            Spacer(modifier = Modifier.width(SpacingMedium))
            Column {
                Text(title, fontWeight = FontWeight.Bold, color = TextPrimary)
                Text(description, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            }
        }
    }
}
