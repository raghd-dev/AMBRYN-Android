package com.moonglow.candles.presentation.screens.wishlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.model.Kit
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.data.model.Workshop
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.*
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@Composable
fun WishlistScreen(repo: MoonGlowRepository) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val wishlist by repo.wishlist.collectAsState()
    val liveReviews by repo.productReviews.collectAsState()
    val workshops by repo.workshops.collectAsState()
    val user by repo.user.collectAsState()

    var selectedProduct by remember { mutableStateOf<Product?>(null) }
    var selectedKit by remember { mutableStateOf<Kit?>(null) }
    var selectedWorkshop by remember { mutableStateOf<Workshop?>(null) }

    val productItems = repo.products
        .filter { wishlist.contains(it.id) }
        .map { WishlistItem(id = it.id, type = WishlistType.PRODUCT, product = it) }

    val kitItems = repo.kits
        .filter { wishlist.contains(it.id) }
        .map { WishlistItem(id = it.id, type = WishlistType.KIT, kit = it) }

    val workshopItems = workshops
        .filter { wishlist.contains(it.id) }
        .map { WishlistItem(id = it.id, type = WishlistType.WORKSHOP, workshop = it) }

    val allItems = productItems + kitItems + workshopItems

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        WishlistHeader(count = allItems.size)

        if (allItems.isEmpty()) {
            EmptyState(
                icon = "🤍",
                title = "No favorites yet",
                message = "Save candles, kits and classes you love."
            )
        } else {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(156.dp),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(allItems) { item ->
                    when (item.type) {
                        WishlistType.PRODUCT -> {
                            item.product?.let { product ->
                                ProductCard(
                                    product = product,
                                    isWishlisted = true,
                                    customerReviewCount = liveReviews[product.id]?.size,
                                    customerRating = liveReviews[product.id]?.takeIf { it.isNotEmpty() }?.map { it.rating }?.average(),
                                    onWishlist = { repo.toggleWishlist(product.id) },
                                    onAdd = { repo.addToCart(product) },
                                    onOpen = { selectedProduct = product }
                                )
                            }
                        }
                        WishlistType.KIT -> {
                            item.kit?.let { kit ->
                                WishlistKitCard(
                                    kit = kit,
                                    onOpen = { selectedKit = kit },
                                    onRemove = { repo.toggleWishlist(kit.id) },
                                    onAdd = { repo.addKitToCart(kit) }
                                )
                            }
                        }
                        WishlistType.WORKSHOP -> {
                            item.workshop?.let { workshop ->
                                WishlistWorkshopCard(
                                    workshop = workshop,
                                    onOpen = { selectedWorkshop = workshop },
                                    onRemove = { repo.toggleWishlist(workshop.id) },
                                    onBook = { repo.bookWorkshop(workshop.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Product Details Dialog with Review
    selectedProduct?.let { product ->
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
            onDismiss = { selectedProduct = null },
            onWishlist = { repo.toggleWishlist(product.id) },
            onAdd = { repo.addToCart(product) },
            onWriteReview = { rating, reviewText ->
                scope.launch {
                    val result = repo.submitReview(product.id, rating, reviewText)
                    if (result.isSuccess) {
                        refreshTrigger++
                        Toast.makeText(context, "Thank you for your review!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, result.exceptionOrNull()?.message ?: "Failed to submit review", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        )
    }

    // Kit Dialog
    selectedKit?.let { kit ->
        AlertDialog(
            onDismissRequest = { selectedKit = null },
            confirmButton = {
                GlowButton(text = "Add Kit to Cart", modifier = Modifier.fillMaxWidth()) {
                    repo.addKitToCart(kit)
                    selectedKit = null
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedKit = null }) {
                    Text("Close")
                }
            },
            title = { Text(kit.name, style = MaterialTheme.typography.headlineMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(kit.description, color = SoftBrown)
                    Text("Level: ${kit.level}", color = GoldDark, fontWeight = FontWeight.Bold)
                    Text(formatPrice(kit.price), color = GoldDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text("Includes:", fontWeight = FontWeight.Bold)
                    kit.includes.forEach { Text("• $it") }
                }
            }
        )
    }

    // Workshop Dialog
    selectedWorkshop?.let { workshop ->
        AlertDialog(
            onDismissRequest = { selectedWorkshop = null },
            confirmButton = {
                GlowButton(
                    text = if (workshop.type == "Course") "Start Course" else "Reserve Seat",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    repo.bookWorkshop(workshop.id)
                    selectedWorkshop = null
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedWorkshop = null }) {
                    Text("Close")
                }
            },
            title = { Text(workshop.title, style = MaterialTheme.typography.headlineMedium) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text(workshop.description, color = SoftBrown)
                    Text("Type: ${workshop.type}", color = GoldDark, fontWeight = FontWeight.Bold)
                    Text("Date: ${workshop.dateLabel}")
                    Text("Duration: ${workshop.duration}")
                    Text(formatPrice(workshop.price), color = GoldDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                    Text(if (workshop.type == "Course") "Online access available" else "${workshop.seatsLeft} seats left", color = SoftBrown)
                }
            }
        )
    }
}

// Helper data classes and composables
private enum class WishlistType { PRODUCT, KIT, WORKSHOP }
private data class WishlistItem(
    val id: String,
    val type: WishlistType,
    val product: Product? = null,
    val kit: Kit? = null,
    val workshop: Workshop? = null
)

@Composable
private fun WishlistHeader(count: Int) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Row {
            Icon(imageVector = Icons.Outlined.Favorite, contentDescription = null, tint = Terracotta)
            Spacer(modifier = Modifier.width(10.dp))
            Text(text = "Wishlist", style = MaterialTheme.typography.displayLarge, color = Charcoal, fontWeight = FontWeight.Bold)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = "$count saved item${if (count == 1) "" else "s"}", color = SoftBrown, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
private fun WishlistKitCard(kit: Kit, onOpen: () -> Unit, onRemove: () -> Unit, onAdd: () -> Unit) {
    WishlistMiniCard(
        title = kit.name,
        subtitle = kit.level,
        price = kit.price,
        icon = Icons.Outlined.Inventory2,
        iconTint = GoldDark,
        onOpen = onOpen,
        onRemove = onRemove,
        onAction = onAdd,
        actionText = "Add"
    )
}

@Composable
private fun WishlistWorkshopCard(workshop: Workshop, onOpen: () -> Unit, onRemove: () -> Unit, onBook: () -> Unit) {
    WishlistMiniCard(
        title = workshop.title,
        subtitle = workshop.type,
        price = workshop.price,
        icon = Icons.Outlined.Spa,
        iconTint = Terracotta,
        onOpen = onOpen,
        onRemove = onRemove,
        onAction = onBook,
        actionText = "Book"
    )
}

@Composable
private fun WishlistMiniCard(
    title: String,
    subtitle: String,
    price: Double,
    icon: ImageVector,
    iconTint: androidx.compose.ui.graphics.Color,
    onOpen: () -> Unit,
    onRemove: () -> Unit,
    onAction: () -> Unit,
    actionText: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(6.dp),
        onClick = onOpen
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row {
                Icon(imageVector = icon, contentDescription = null, tint = iconTint)
                Spacer(modifier = Modifier.weight(1f))
                IconButton(onClick = onRemove) {
                    Icon(imageVector = Icons.Outlined.Favorite, contentDescription = null, tint = Terracotta)
                }
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium, color = Charcoal, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, color = SoftBrown)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = formatPrice(price), color = GoldDark, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(14.dp))
            GlowButton(text = actionText, modifier = Modifier.fillMaxWidth()) { onAction() }
        }
    }
}