package com.easylabour.app.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easylabour.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    username: String,
    email: String,
    userType: String,
    onLogout: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold, color = AlmostBlack) },
                actions = {
                    IconButton(onClick = {}) { Icon(Icons.Outlined.Notifications, contentDescription = null, tint = AlmostBlack) }
                    Box {
                        IconButton(onClick = { showMenu = true }) { 
                            Icon(Icons.Default.Settings, contentDescription = "Settings", tint = AlmostBlack) 
                        }
                        DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                            DropdownMenuItem(
                                text = { Text("Logout") },
                                onClick = {
                                    showMenu = false
                                    onLogout()
                                }
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = OffWhite
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding).fillMaxSize().padding(16.dp)) {
            item {
                // Header
                Card(colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Surface(modifier = Modifier.size(72.dp), shape = CircleShape, color = LightGray) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.padding(16.dp))
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(username, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = AlmostBlack)
                                Spacer(modifier = Modifier.width(8.dp))
                                Surface(color = BlushPink, shape = RoundedCornerShape(4.dp)) {
                                    Text("Verified", fontSize = 10.sp, modifier = Modifier.padding(4.dp), color = DeepPlum)
                                }
                            }
                            Text("+91 98765 43210", fontSize = 14.sp, color = DarkGray)
                            Text(email, fontSize = 14.sp, color = DarkGray)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Wallet/Points
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatsCard("Easy Wallet", "₹1,250.00", "View Wallet", Modifier.weight(1f))
                    StatsCard("My Coupons", "4 Offers", "View Coupons", Modifier.weight(1f))
                    StatsCard("Loyalty", "320 pts", "View Points", Modifier.weight(1f))
                }
                Spacer(modifier = Modifier.height(16.dp))
                
                // Membership
                Card(colors = CardDefaults.cardColors(containerColor = Color(0xFF3F1B4E))) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.WorkspacePremium, contentDescription = null, tint = Gold, modifier = Modifier.size(32.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Easy Labour Plus", fontWeight = FontWeight.Bold, color = PureWhite)
                            Text("Exclusive benefits for members", fontSize = 12.sp, color = PureWhite.copy(alpha = 0.8f))
                        }
                        Button(onClick = {}, colors = ButtonDefaults.buttonColors(containerColor = PureWhite, contentColor = DeepPlum)) { Text("Upgrade") }
                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Booking Stats
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceAround) {
                    BookingStat("12", "Upcoming")
                    BookingStat("3", "In Progress")
                    BookingStat("18", "Completed")
                    BookingStat("2", "Cancelled")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Settings List
                Column {
                    SettingsItem(Icons.Default.Person, "Personal Information")
                    SettingsItem(Icons.Default.LocationOn, "Addresses")
                    SettingsItem(Icons.Default.Payment, "Payment Methods")
                    SettingsItem(Icons.Default.Notifications, "Notifications")
                    SettingsItem(Icons.Default.HeadsetMic, "Help & Support")
                    SettingsItem(Icons.Default.PrivacyTip, "Privacy Policy")
                    SettingsItem(Icons.Default.Info, "About Easy Labour")
                }
                Spacer(modifier = Modifier.height(16.dp))

                // Refer & Earn
                Card(colors = CardDefaults.cardColors(containerColor = PureWhite)) {
                    Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = DeepPlum, modifier = Modifier.size(40.dp))
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text("Refer & Earn", fontWeight = FontWeight.Bold, color = AlmostBlack)
                            Text("Refer friends and earn rewards!", fontSize = 12.sp, color = DarkGray)
                        }
                        Icon(Icons.Default.ChevronRight, contentDescription = null)
                    }
                }
            }
        }
    }
}

@Composable
fun StatsCard(label: String, value: String, action: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier, colors = CardDefaults.cardColors(containerColor = PureWhite)) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(label, fontSize = 10.sp, color = DarkGray)
            Text(value, fontWeight = FontWeight.Bold, color = AlmostBlack)
            Text(action, fontSize = 10.sp, color = DeepPlum)
        }
    }
}

@Composable
fun BookingStat(count: String, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(count, fontWeight = FontWeight.Bold, fontSize = 16.sp, color = AlmostBlack)
        Text(label, fontSize = 10.sp, color = DarkGray)
    }
}

@Composable
fun SettingsItem(icon: androidx.compose.ui.graphics.vector.ImageVector, title: String) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp).clickable {}, verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = DeepPlum)
        Spacer(modifier = Modifier.width(16.dp))
        Text(title, modifier = Modifier.weight(1f), color = AlmostBlack)
        Icon(Icons.Default.ChevronRight, contentDescription = null, tint = DarkGray)
    }
}
