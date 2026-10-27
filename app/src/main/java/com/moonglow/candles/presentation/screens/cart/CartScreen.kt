package com.moonglow.candles.presentation.screens.cart

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.ShoppingBag
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.model.CartItem
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.EmptyState
import com.moonglow.candles.presentation.components.GlowButton
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.domain.checkout.calculateDemoTotals
import com.moonglow.candles.domain.cart.consolidateCartItems

@Composable
fun CartScreen(
    repo: MoonGlowRepository,
    onCheckout: () -> Unit
) {
    val cart by repo.cart.collectAsState()
    var selectedItem by remember { mutableStateOf<CartItem?>(null) }

    // Defensive normalization: repeated legacy rows must not crash LazyColumn.
    val displayCart = remember(cart) { consolidateCartItems(cart) }
    val subtotal = calculateDemoTotals(displayCart).subtotal

    // An opaque surface keeps other navigation destinations from bleeding through
    // during navigation transitions or emulator screenshots.
    Surface(modifier = Modifier.fillMaxSize(), color = Background) {
        Column(modifier = Modifier.fillMaxSize()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Outlined.ShoppingBag, contentDescription = null, tint = GoldDark)
                Spacer(Modifier.width(12.dp))
                Column {
                    Text("My Cart", style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                    Text(
                        "${displayCart.sumOf { it.quantity }} item${if (displayCart.sumOf { it.quantity } == 1) "" else "s"}",
                        color = SoftBrown,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            if (displayCart.isEmpty()) {
                Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                    EmptyState(
                        icon = "🛒",
                        title = "Your cart is empty",
                        message = "Add your favorite candles or kits to get started."
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 12.dp)
                ) {
                    items(displayCart, key = { it.product.id }) { item ->
                        CartItemCard(
                            item = item,
                            onOpen = { selectedItem = item },
                            onIncrease = { repo.changeQty(item.product.id, 1) },
                            onDecrease = { repo.changeQty(item.product.id, -1) },
                            onRemove = { repo.removeFromCart(item.product.id) }
                        )
                    }
                }
                Surface(
                    color = CardWhite,
                    shadowElevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 26.dp, topEnd = 26.dp)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Subtotal", color = SoftBrown, style = MaterialTheme.typography.titleMedium)
                            Text(
                                formatPrice(subtotal),
                                style = MaterialTheme.typography.titleLarge,
                                color = GoldDark,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            "Shipping is calculated at checkout",
                            color = SoftBrown,
                            style = MaterialTheme.typography.bodySmall
                        )
                        Spacer(Modifier.height(12.dp))
                        GlowButton("Continue to Checkout", Modifier.fillMaxWidth()) { onCheckout() }
                    }
                }
            }
        }
    }

    displayCart.firstOrNull { it.product.id == selectedItem?.product?.id }?.let { item ->
        CartItemDetailsDialog(
            item = item,
            onDismiss = { selectedItem = null },
            onIncrease = { repo.changeQty(item.product.id, 1) },
            onDecrease = { repo.changeQty(item.product.id, -1) },
            onRemove = {
                repo.removeFromCart(item.product.id)
                selectedItem = null
            }
        )
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onOpen: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val product = item.product

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 7.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(product.imageRes),
                contentDescription = product.name,
                modifier = Modifier.size(82.dp).clip(RoundedCornerShape(16.dp)),
                contentScale = ContentScale.Crop
            )
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 2
                )
                Text(formatPrice(product.price), color = GoldDark, fontWeight = FontWeight.Bold)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalIconButton(
                        onClick = onDecrease,
                        modifier = Modifier.size(36.dp)
                    ) { Icon(Icons.Outlined.Remove, contentDescription = "Decrease quantity") }
                    Text(
                        item.quantity.toString(),
                        modifier = Modifier.widthIn(min = 36.dp),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        fontWeight = FontWeight.Bold
                    )
                    FilledTonalIconButton(
                        onClick = onIncrease,
                        modifier = Modifier.size(36.dp)
                    ) { Icon(Icons.Outlined.Add, contentDescription = "Increase quantity") }
                }
            }
            IconButton(onClick = onRemove, modifier = Modifier.size(40.dp)) {
                Icon(Icons.Outlined.Delete, contentDescription = "Remove ${product.name}", tint = Terracotta)
            }
        }
    }
}

@Composable
private fun CartItemDetailsDialog(
    item: CartItem,
    onDismiss: () -> Unit,
    onIncrease: () -> Unit,
    onDecrease: () -> Unit,
    onRemove: () -> Unit
) {
    val product: Product = item.product

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlowButton("Continue Shopping", Modifier.fillMaxWidth()) {
                onDismiss()
            }
        },
        dismissButton = {
            TextButton(onClick = onRemove) {
                Text("Remove", color = Terracotta)
            }
        },
        title = {
            Text(product.name, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(
                    painter = painterResource(product.imageRes),
                    contentDescription = product.name,
                    modifier = Modifier.fillMaxWidth().height(220.dp).clip(RoundedCornerShape(22.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(product.description, color = SoftBrown)

                Text(
                    "${formatPrice(product.price)} each",
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )

                Text("Quantity: ${item.quantity}", fontWeight = FontWeight.Bold)

                Text("Total: ${formatPrice(product.price * item.quantity)}", color = GoldDark)

                if (product.scentNotes.isNotEmpty()) {
                    Text("Notes: ${product.scentNotes.joinToString()}", color = SoftBrown)
                }

                Text("Burn time: ${product.burnTime}", color = SoftBrown)

                Row(verticalAlignment = Alignment.CenterVertically) {
                    FilledTonalButton(onClick = onDecrease) { Text("-") }
                    Spacer(Modifier.width(12.dp))
                    Text(item.quantity.toString(), fontWeight = FontWeight.Bold)
                    Spacer(Modifier.width(12.dp))
                    FilledTonalButton(onClick = onIncrease) { Text("+") }
                }
            }
        }
    )
}