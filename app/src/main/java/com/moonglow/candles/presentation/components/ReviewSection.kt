package com.moonglow.candles.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.Gold
import com.moonglow.candles.core.theme.GoldDark

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            IconButton(onClick = { onRatingChanged(i) }) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rate $i stars",
                    tint = if (i <= rating) Gold else GoldDark.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
fun ReviewDialog(
    productName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, reviewText: String) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rate $productName") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                StarRatingBar(rating = rating, onRatingChanged = { rating = it })

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Write your review (optional)") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSubmit(rating, reviewText) },
                enabled = rating > 0 && !isSubmitting
            ) {
                if (isSubmitting) {
                    Text("Submitting...")
                } else {
                    Text("Submit Review")
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
        shape = RoundedCornerShape(24.dp)
    )
}