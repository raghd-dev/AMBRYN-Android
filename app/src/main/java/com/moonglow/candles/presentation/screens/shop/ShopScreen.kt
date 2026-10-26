package com.moonglow.candles.presentation.screens.shop

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.background
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.moonglow.candles.core.theme.Background
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.ProductCard
import com.moonglow.candles.presentation.components.ProductDetailsDialog
import com.moonglow.candles.presentation.components.SectionHeader
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun ShopScreen(
    repo: MoonGlowRepository,
    nav: NavController
) {
    val scope = rememberCoroutineScope()
    val wishlist by repo.wishlist.collectAsState()
    val liveReviews by repo.productReviews.collectAsState()
    val user by repo.user.collectAsState()
    var filter by remember { mutableStateOf("All") }
    var selected by remember { mutableStateOf<Product?>(null) }

    val categories = listOf("All", "Sweet", "Woody", "Floral", "Fresh")
    val items = repo.products.filter { filter == "All" || it.category == filter }

    Column(
        modifier = Modifier.fillMaxSize().background(Background)
    ) {
        SectionHeader("Premium Candles")

        ScrollableTabRow(
            selectedTabIndex = categories.indexOf(filter),
            edgePadding = 16.dp,
            containerColor = Color.Transparent
        ) {
            categories.forEach { category ->
                Tab(
                    selected = filter == category,
                    onClick = { filter = category },
                    text = { Text(category) }
                )
            }
        }

        LazyVerticalGrid(
            columns = GridCells.Adaptive(156.dp),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(items) { product ->
                ProductCard(
                    product = product,
                    isWishlisted = wishlist.contains(product.id),
                    customerReviewCount = liveReviews[product.id]?.size,
                    customerRating = liveReviews[product.id]?.takeIf { it.isNotEmpty() }?.map { it.rating }?.average(),
                    onWishlist = { repo.toggleWishlist(product.id) },
                    onAdd = { repo.addToCart(product) },  // ← This line
                    onOpen = { selected = product }
                )
            }
        }
    }

    selected?.let { product ->
        var refreshTrigger by remember { mutableStateOf(0) }
        val productReviews = remember(refreshTrigger) {
            repo.getProductReviews(product.id)
        }

        ProductDetailsDialog(
            product = product,
            isWishlisted = wishlist.contains(product.id),
            userEmail = user?.email,
            userName = user?.name,
            productReviews = productReviews,
            onDismiss = { selected = null },
            onWishlist = { repo.toggleWishlist(product.id) },
            onAdd = { repo.addToCart(product) },
            onWriteReview = { rating, reviewText ->
                scope.launch {
                    val result = repo.submitReview(product.id, rating, reviewText)
                    if (result.isSuccess) {
                        refreshTrigger++
                        // Show success
                    }
                }
            }
        )
    }
}