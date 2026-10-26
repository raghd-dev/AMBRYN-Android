package com.moonglow.candles.presentation.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.moonglow.candles.R
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.data.remote.ProductReview

import androidx.compose.foundation.BorderStroke
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.outlined.ThumbUp

@Composable
fun GlowButton(text: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = modifier.height(54.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Gold),
        shape = MaterialTheme.shapes.medium,
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 5.dp, pressedElevation = 9.dp)
    ) {
        Text(text, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun FlameLoader(modifier: Modifier = Modifier) {
    val transition = rememberInfiniteTransition(label = "flame")
    val scale by transition.animateFloat(
        initialValue = 0.88f,
        targetValue = 1.12f,
        animationSpec = infiniteRepeatable(tween(650), RepeatMode.Reverse),
        label = "scale"
    )

    Column(modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            Modifier
                .size(58.dp)
                .scale(scale)
                .background(
                    Brush.verticalGradient(listOf(Color(0xFFFFE0A3), Gold, Terracotta)),
                    CircleShape
                )
        )

        Spacer(Modifier.height(16.dp))

        Text(
            "Igniting warmth...",
            color = Charcoal.copy(.7f),
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun StarRatingBar(
    rating: Int,
    onRatingChanged: (Int) -> Unit,
    modifier: Modifier = Modifier,
    starSize: Int = 24
) {
    Row(modifier = modifier) {
        for (i in 1..5) {
            IconButton(
                onClick = { onRatingChanged(i) },
                modifier = Modifier.size(starSize.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Filled.Star else Icons.Outlined.Star,
                    contentDescription = "Rate $i stars",
                    tint = if (i <= rating) Gold else GoldDark.copy(alpha = 0.5f),
                    modifier = Modifier.size((starSize - 4).dp)
                )
            }
        }
    }
}

@Composable
fun StarRatingDisplay(
    rating: Double,
    reviewCount: Int,
    modifier: Modifier = Modifier,
    starSize: Int = 14
) {
    if (reviewCount <= 0) {
        Text(
            text = "No reviews yet",
            modifier = modifier,
            style = MaterialTheme.typography.bodySmall,
            color = SoftBrown
        )
        return
    }
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = if (index < rating.toInt()) Icons.Filled.Star else Icons.Outlined.Star,
                contentDescription = null,
                tint = Gold,
                modifier = Modifier.size(starSize.dp)
            )
        }
        Text(
            text = String.format(java.util.Locale.US, "%.1f", rating),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Bold,
            color = Charcoal
        )
        Text(
            text = "($reviewCount)",
            style = MaterialTheme.typography.bodySmall,
            color = SoftBrown
        )
    }
}

@Composable
fun ReviewCard(review: ProductReview) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header Row - User and Date
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // User Avatar Circle
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(
                                    listOf(GoldLight, GoldDark)
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = review.userName.take(1).uppercase(),
                            color = CardWhite,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    Column {
                        Text(
                            text = review.userName,
                            fontWeight = FontWeight.SemiBold,
                            color = Charcoal,
                            fontSize = 14.sp
                        )
                        Text(
                            text = review.createdAt.take(10),
                            color = SoftBrown,
                            fontSize = 10.sp
                        )
                    }
                }

                // Rating Stars
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    repeat(5) { index ->
                        Icon(
                            imageVector = if (index < review.rating) Icons.Filled.Star else Icons.Outlined.Star,
                            contentDescription = null,
                            tint = if (index < review.rating) Gold else GoldLight,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Review Text
            if (review.reviewText.isNotBlank()) {
                Text(
                    text = review.reviewText,
                    color = SoftBrown,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            } else {
                Text(
                    text = "No comment provided",
                    color = SoftBrown.copy(alpha = 0.6f),
                    fontSize = 13.sp,
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            // Optional: Helpful button
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(
                    onClick = { },
                    enabled = false,
                    modifier = Modifier.height(28.dp)
                ) {
                    Icon(
                        Icons.Outlined.ThumbUp,
                        contentDescription = "Helpful",
                        modifier = Modifier.size(12.dp),
                        tint = GoldDark
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Helpful",
                        fontSize = 11.sp,
                        color = SoftBrown
                    )
                }
            }
        }
    }
}
@Composable
fun ReviewsList(
    productId: String,
    reviews: List<ProductReview>,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Header with count
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Customer Reviews",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Surface(
                shape = RoundedCornerShape(20.dp),
                color = GoldLight.copy(alpha = 0.2f)
            ) {
                Text(
                    text = "${reviews.size} ${if (reviews.size == 1) "Review" else "Reviews"}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                    fontSize = 12.sp,
                    color = GoldDark,
                    fontWeight = FontWeight.Medium
                )
            }
        }

        if (reviews.isEmpty()) {
            // Empty State Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Cream.copy(alpha = 0.5f)
                ),
                border = BorderStroke(1.dp, DividerColor)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "✨",
                        fontSize = 32.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "No reviews yet",
                        fontWeight = FontWeight.SemiBold,
                        color = Charcoal,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Be the first to share your experience!",
                        color = SoftBrown,
                        fontSize = 13.sp
                    )
                }
            }
        } else {
            // Summary Card
            val avgRating = reviews.map { it.rating }.average()
            val totalReviews = reviews.size

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Cream
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = String.format("%.1f", avgRating),
                            style = MaterialTheme.typography.displayMedium,
                            fontWeight = FontWeight.Bold,
                            color = GoldDark
                        )
                        StarRatingDisplay(
                            rating = avgRating,
                            reviewCount = totalReviews,
                            starSize = 14
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = DividerColor
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "$totalReviews",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal
                        )
                        Text(
                            text = if (totalReviews == 1) "Review" else "Reviews",
                            color = SoftBrown,
                            fontSize = 12.sp
                        )
                    }

                    Divider(
                        modifier = Modifier
                            .height(40.dp)
                            .width(1.dp),
                        color = DividerColor
                    )

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "${reviews.count { it.rating >= 4 } * 100 / reviews.size}%",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = Charcoal
                        )
                        Text(
                            text = "Recommended",
                            color = SoftBrown,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Reviews List
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(reviews) { review ->
                    ReviewCard(review = review)
                }
            }
        }
    }
}

@Composable
fun ReviewDialog(
    productName: String,
    userEmail: String,
    userName: String,
    onDismiss: () -> Unit,
    onSubmit: (rating: Int, reviewText: String) -> Unit
) {
    var rating by remember { mutableStateOf(0) }
    var reviewText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Rate $productName", style = MaterialTheme.typography.headlineSmall)
                Text("Share your experience", style = MaterialTheme.typography.bodySmall, color = SoftBrown)
            }
        },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StarRatingBar(
                    rating = rating,
                    onRatingChanged = { rating = it },
                    starSize = 32
                )

                OutlinedTextField(
                    value = reviewText,
                    onValueChange = { reviewText = it },
                    label = { Text("Write your review (optional)") },
                    placeholder = { Text("I love this candle! The scent is amazing...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    isSubmitting = true
                    onSubmit(rating, reviewText)
                    onDismiss()
                },
                enabled = rating > 0 && !isSubmitting,
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(16.dp)
            ) {
                if (isSubmitting) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Submitting...")
                } else {
                    Text("Submit Review", fontWeight = FontWeight.Bold)
                }
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = SoftBrown)
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = CardWhite
    )
}

@Composable
fun ProductCard(
    product: Product,
    isWishlisted: Boolean,
    onWishlist: () -> Unit,
    onAdd: () -> Unit,
    onOpen: () -> Unit,
    modifier: Modifier = Modifier.fillMaxWidth(),
    customerRating: Double? = null,
    customerReviewCount: Int? = null
) {
    Card(
        modifier = modifier.clickable { onOpen() },
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 10.dp, pressedElevation = 14.dp),
        shape = MaterialTheme.shapes.large
    ) {
        Box {
            Image(
                painter = painterResource(id = productDrawable(product)),
                contentDescription = product.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp),
                contentScale = ContentScale.Crop
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .background(
                        Brush.verticalGradient(
                            listOf(
                                Color.Transparent,
                                Color.Black.copy(.18f)
                            )
                        )
                    )
            )

            IconButton(
                onClick = onWishlist,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(10.dp)
                    .clip(CircleShape)
                    .background(CardWhite.copy(.92f))
            ) {
                Icon(
                    imageVector = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                    contentDescription = "Wishlist",
                    tint = if (isWishlisted) Terracotta else Charcoal
                )
            }
        }

        Column(Modifier.padding(16.dp)) {
            Text(
                product.name,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                minLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(4.dp))

            Text(
                product.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Charcoal.copy(.65f),
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(Modifier.height(8.dp))

            StarRatingDisplay(
                rating = customerRating ?: product.avgRating,
                reviewCount = customerReviewCount ?: product.reviewCount,
                starSize = 12
            )

            Spacer(Modifier.height(8.dp))

            Row(
                Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    formatPrice(product.price),
                    color = GoldDark,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                FilledTonalIconButton(
                    onClick = onAdd,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = GoldLight.copy(.22f)
                    )
                ) {
                    Icon(
                        Icons.Outlined.ShoppingCart,
                        contentDescription = "Add to cart",
                        tint = GoldDark
                    )
                }
            }
        }
    }
}

@Composable
fun SectionHeader(title: String, action: String? = null, onAction: () -> Unit = {}) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(title, style = MaterialTheme.typography.headlineMedium)

        if (action != null) {
            TextButton(onClick = onAction) {
                Text(action, color = GoldDark, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun EmptyState(icon: String, title: String, message: String) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(36.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(icon, style = MaterialTheme.typography.displayLarge)

        Spacer(Modifier.height(8.dp))

        Text(title, style = MaterialTheme.typography.headlineMedium)

        Spacer(Modifier.height(8.dp))

        Text(message, color = Charcoal.copy(.65f))
    }
}

@Composable
fun ProductDetailsDialog(
    product: Product,
    isWishlisted: Boolean,
    userEmail: String?,
    userName: String?,
    productReviews: List<ProductReview>,
    onDismiss: () -> Unit,
    onWishlist: () -> Unit,
    onAdd: () -> Unit,
    onWriteReview: ((rating: Int, reviewText: String) -> Unit)? = null
) {
    var showReviewDialog by remember { mutableStateOf(false) }
    // Display only genuine loaded reviews; don't pair seeded ratings with zero reviews.
    val avgRating = if (productReviews.isEmpty()) 0.0 else productReviews.map { it.rating }.average()
    val reviewCount = productReviews.size

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlowButton("Add to Cart", Modifier.fillMaxWidth()) {
                onAdd()
                onDismiss()
            }
        },
        dismissButton = {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TextButton(onClick = onDismiss) {
                    Text("Close", color = SoftBrown)
                }

                if (onWriteReview != null && userEmail != null && userName != null) {
                    TextButton(onClick = { showReviewDialog = true }) {
                        Icon(
                            Icons.Outlined.Star,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp),
                            tint = Gold
                        )
                        Spacer(Modifier.width(4.dp))
                        Text("Write Review", color = Gold)
                    }
                }
            }
        },
        title = {
            Column {
                Text(product.name, style = MaterialTheme.typography.headlineMedium, color = Charcoal)
                StarRatingDisplay(rating = avgRating, reviewCount = reviewCount, starSize = 16)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 500.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Image(
                    painter = painterResource(id = productDrawable(product)),
                    contentDescription = product.name,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(MaterialTheme.shapes.large),
                    contentScale = ContentScale.Crop
                )

                Text(product.description, color = SoftBrown)

                Text(
                    "Price: ${formatPrice(product.price)}",
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )

                Text("Burn time: ${product.burnTime}", color = SoftBrown)

                if (product.scentNotes.isNotEmpty()) {
                    Text("Scent Notes:", fontWeight = FontWeight.Bold, color = Charcoal)
                    Text(product.scentNotes.joinToString(" • "), color = SoftBrown)
                }

                Divider(color = DividerColor)

                if (productReviews.isNotEmpty()) {
                    Text(
                        text = "What others say:",
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    productReviews.take(2).forEach { review ->
                        ReviewCard(review = review)
                    }
                    if (productReviews.size > 2) {
                        Text(
                            text = "${productReviews.size} customer reviews in total",
                            color = SoftBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }
                } else {
                    Text(
                        text = "No reviews yet. Be the first to review!",
                        color = SoftBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                TextButton(
                    onClick = onWishlist,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        contentDescription = null,
                        tint = Terracotta
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        if (isWishlisted) "Remove from Wishlist" else "Add to Wishlist",
                        color = Terracotta
                    )
                }
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = CardWhite
    )

    if (showReviewDialog && onWriteReview != null) {
        ReviewDialog(
            productName = product.name,
            userEmail = userEmail ?: "",
            userName = userName ?: "Guest",
            onDismiss = { showReviewDialog = false },
            onSubmit = { rating, reviewText ->
                onWriteReview(rating, reviewText)
                showReviewDialog = false
            }
        )
    }
}

private fun productDrawable(product: Product): Int {
    val name = product.name.lowercase()

    return when {
        "vanilla" in name -> R.drawable.vanilla_glow
        "amber" in name -> R.drawable.amber_night
        "cedar" in name || "cedarwood" in name -> R.drawable.cedarwood
        "lavender" in name -> R.drawable.lavender_dream
        "rose" in name -> R.drawable.rose_calm
        "sea" in name || "salt" in name -> R.drawable.sea_salt
        "linen" in name || "fresh" in name -> R.drawable.fresh_linen
        "pumpkin" in name || "spiced" in name -> R.drawable.spiced_pumpkin
        "beginner" in name -> R.drawable.beginner_kit
        "premium" in name -> R.drawable.premium_kit
        "gift" in name -> R.drawable.gift_kit
        "kit" in name -> R.drawable.beginner_kit
        else -> R.drawable.collection_candles
    }
}