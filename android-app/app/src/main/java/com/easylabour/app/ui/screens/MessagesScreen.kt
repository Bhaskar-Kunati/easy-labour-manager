package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.easylabour.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesScreen(onNavigateBack: () -> Unit) {
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("All", "Bookings", "Offers", "Support")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Messages", fontWeight = FontWeight.Bold, color = AlmostBlack, fontSize = 24.sp) },
                actions = {
                    BadgedBox(badge = { Badge { Text("3") } }) {
                        IconButton(onClick = {}) { Icon(Icons.Default.ChatBubbleOutline, contentDescription = null, tint = AlmostBlack) }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = PureWhite)
            )
        },
        containerColor = OffWhite
    ) { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            // Search Bar
            OutlinedTextField(
                value = "",
                onValueChange = {},
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp).height(50.dp),
                placeholder = { Text("Search messages...", fontSize = 12.sp) },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
                trailingIcon = { Icon(Icons.Default.Tune, contentDescription = null, modifier = Modifier.size(18.dp)) },
                shape = RoundedCornerShape(12.dp),
                textStyle = LocalTextStyle.current.copy(fontSize = 12.sp)
            )

            // Tabs
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = OffWhite,
                indicator = { TabRowDefaults.Indicator(Modifier.tabIndicatorOffset(it[selectedTabIndex]), color = Color.Transparent) }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Surface(
                                color = if (selectedTabIndex == index) DeepPlum else Color.Transparent,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(title, modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp), color = if (selectedTabIndex == index) PureWhite else AlmostBlack)
                            }
                        }
                    )
                }
            }

            // Message List
            LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(5) {
                    MessageCard()
                }
            }
        }
    }
}

@Composable
fun MessageCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(0.dp),
        colors = CardDefaults.cardColors(containerColor = PureWhite)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Surface(modifier = Modifier.size(56.dp), shape = CircleShape, color = LightGray) {
                // Placeholder for profile image
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                    Text("Ramesh Kumar", fontWeight = FontWeight.Bold, color = AlmostBlack)
                    Text("09:32 AM", fontSize = 12.sp, color = DarkGray)
                }
                Text("Plumber", fontSize = 12.sp, color = DeepPlum)
                Spacer(modifier = Modifier.height(4.dp))
                Text("Hi, I am on my way...", fontSize = 14.sp, color = AlmostBlack, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Schedule, contentDescription = null, modifier = Modifier.size(14.dp), tint = DeepPlum)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Active Booking • EL2407210003", fontSize = 12.sp, color = DarkGray)
                }
            }
            Spacer(modifier = Modifier.width(8.dp))
            Surface(modifier = Modifier.size(24.dp), shape = CircleShape, color = DeepPlum) {
                Box(contentAlignment = Alignment.Center) { Text("2", color = PureWhite, fontSize = 12.sp) }
            }
        }
    }
}
