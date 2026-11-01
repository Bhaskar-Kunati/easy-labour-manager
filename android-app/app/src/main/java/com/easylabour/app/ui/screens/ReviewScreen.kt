package com.easylabour.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.easylabour.app.data.model.ReviewDto
import com.easylabour.app.viewmodel.BookingViewModel
import com.easylabour.app.viewmodel.ReviewViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    bookingId: String,
    raterId: String,
    rateeId: String,
    onReviewSubmitted: () -> Unit,
    onNavigateBack: () -> Unit,
    reviewViewModel: ReviewViewModel = hiltViewModel(),
    bookingViewModel: BookingViewModel = hiltViewModel()
) {
    var rating by remember { mutableStateOf(5) }
    var comment by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Submit Feedback", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .padding(24.dp)
                .verticalScroll(scrollState),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            Text(
                "How was your experience?",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFF0F172A)
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                (1..5).forEach { index ->
                    IconButton(onClick = { rating = index }) {
                        Icon(
                            imageVector = if (index <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                            contentDescription = null,
                            tint = if (index <= rating) Color(0xFFF59E0B) else Color.Gray,
                            modifier = Modifier.size(40.dp)
                        )
                    }
                }
            }

            OutlinedTextField(
                value = comment,
                onValueChange = { comment = it },
                label = { Text("Write your feedback") },
                modifier = Modifier.fillMaxWidth().height(150.dp),
                placeholder = { Text("Tell us what went well or what could be improved...") },
                shape = RoundedCornerShape(16.dp)
            )

            Button(
                onClick = {
                    isSubmitting = true
                    scope.launch {
                        try {
                            val review = ReviewDto(
                                null,
                                bookingId.toLong(),
                                raterId,
                                rateeId,
                                rating,
                                comment,
                                null
                            )
                            reviewViewModel.createReview(review)
                            
                            // Update booking status to COMPLETED
                            bookingViewModel.updateBookingStatus(bookingId, "COMPLETED")
                            
                            onReviewSubmitted()
                        } catch (e: Exception) {
                            e.printStackTrace()
                        } finally {
                            isSubmitting = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth().height(56.dp),
                shape = RoundedCornerShape(16.dp),
                enabled = !isSubmitting && comment.isNotBlank(),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0F172A))
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(color = Color.White, modifier = Modifier.size(24.dp))
                } else {
                    Text("Submit Review", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
