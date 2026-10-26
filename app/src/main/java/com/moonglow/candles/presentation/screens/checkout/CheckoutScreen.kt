package com.moonglow.candles.presentation.screens.checkout

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import kotlinx.coroutines.launch
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.GlowButton
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.domain.checkout.calculateDemoTotals
import com.moonglow.candles.domain.checkout.validateDemoDelivery

@Composable
fun CheckoutScreen(
    repo: MoonGlowRepository,
    onSuccess: () -> Unit
) {
    val cart by repo.cart.collectAsState()

    var name by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    val payment = "Demo payment"

    var error by remember { mutableStateOf<String?>(null) }
    var submitting by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val totals = calculateDemoTotals(cart)
    val subtotal = totals.subtotal
    val shipping = totals.shipping
    val total = totals.total

    // The order button stays reachable even when a long checkout form needs scrolling.
    Column(modifier = Modifier.fillMaxSize().background(Background)) {
        Column(
            modifier = Modifier.weight(1f).fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 20.dp)
        ) {
            CheckoutHeader()

            if (cart.isNotEmpty()) {
                OrderItemsPreview(repo)
            } else {
                Text(
                    text = "Your cart is empty. Return to the shop to add an item.",
                    modifier = Modifier.padding(20.dp),
                    color = SoftBrown
                )
            }

            DeliveryCard(
                name = name,
                onNameChange = { name = it },
                phone = phone,
                onPhoneChange = { phone = it },
                address = address,
                onAddressChange = { address = it }
            )

            Text(
                text = "University demo only — no payment will be collected. Do not enter card details.",
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 12.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = SoftBrown
            )
            Text(
                text = "Phone number is checked for this demo but is not saved with the order.",
                modifier = Modifier.padding(horizontal = 22.dp, vertical = 4.dp),
                color = SoftBrown,
                style = MaterialTheme.typography.bodySmall
            )
            OrderSummaryCard(subtotal, shipping, total)
        }

        Surface(
            color = CardWhite,
            shadowElevation = 12.dp,
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp)
            ) {
                error?.let {
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(bottom = 8.dp),
                        fontWeight = FontWeight.SemiBold
                    )
                }
                Button(
                    modifier = Modifier.fillMaxWidth().heightIn(min = 54.dp),
                    enabled = !submitting && cart.isNotEmpty(),
                    colors = ButtonDefaults.buttonColors(containerColor = Gold),
                    shape = RoundedCornerShape(18.dp),
                    onClick = {
                        error = validateDemoDelivery(name, phone, address)
                        if (error == null) {
                            submitting = true
                            scope.launch {
                                repo.placeOrder(name, address, payment)
                                    .onSuccess { onSuccess() }
                                    .onFailure { error = it.message ?: "Could not save your demo order." }
                                submitting = false
                            }
                        }
                    }
                ) {
                    if (submitting) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(Modifier.width(10.dp))
                        Text("Saving demo order...")
                    } else {
                        Text("Place Demo Order • ${formatPrice(total)}", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

}

@Composable
private fun CheckoutHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(horizontal = 22.dp, vertical = 28.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(GoldLight.copy(alpha = 0.25f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Outlined.Lock, null, tint = GoldDark)
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column {
                Text(
                    text = "Demo Checkout",
                    style = MaterialTheme.typography.displayLarge,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "University demo — no real payment is collected.",
                    color = SoftBrown,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}

@Composable
private fun OrderItemsPreview(repo: MoonGlowRepository) {
    val cart by repo.cart.collectAsState()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Text(
                text = "Your Items",
                style = MaterialTheme.typography.headlineMedium,
                color = Charcoal,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                items(cart) { item ->
                    Column(
                        modifier = Modifier.width(95.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = item.product.imageRes),
                            contentDescription = item.product.name,
                            modifier = Modifier
                                .size(82.dp)
                                .clip(RoundedCornerShape(20.dp)),
                            contentScale = ContentScale.Crop
                        )

                        Spacer(modifier = Modifier.height(6.dp))

                        Text(
                            text = item.product.name,
                            maxLines = 1,
                            color = Charcoal,
                            style = MaterialTheme.typography.labelLarge
                        )

                        Text(
                            text = "x${item.quantity}",
                            color = GoldDark,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DeliveryCard(
    name: String,
    onNameChange: (String) -> Unit,
    phone: String,
    onPhoneChange: (String) -> Unit,
    address: String,
    onAddressChange: (String) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            Text(
                text = "Delivery Details",
                style = MaterialTheme.typography.headlineMedium,
                color = Charcoal,
                fontWeight = FontWeight.Bold
            )

            OutlinedTextField(
                value = name,
                onValueChange = onNameChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Full name") },
                leadingIcon = { Icon(Icons.Outlined.Person, null) },
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = phone,
                onValueChange = onPhoneChange,
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Phone number") },
                leadingIcon = { Icon(Icons.Outlined.Phone, null) },
                shape = RoundedCornerShape(18.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = address,
                onValueChange = onAddressChange,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(115.dp),
                label = { Text("Delivery address") },
                leadingIcon = { Icon(Icons.Outlined.Home, null) },
                shape = RoundedCornerShape(18.dp),
                maxLines = 3
            )
        }
    }
}

@Composable
private fun OrderSummaryCard(
    subtotal: Double,
    shipping: Double,
    total: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = Charcoal),
        elevation = CardDefaults.cardElevation(12.dp)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Text(
                text = "Order Summary",
                color = CardWhite,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(18.dp))

            CheckoutRow("Subtotal", formatPrice(subtotal))
            Spacer(modifier = Modifier.height(10.dp))
            CheckoutRow("Shipping", if (shipping == 0.0) "FREE" else formatPrice(shipping))
            Spacer(modifier = Modifier.height(14.dp))
            Divider(color = CardWhite.copy(alpha = 0.18f))
            Spacer(modifier = Modifier.height(14.dp))
            CheckoutRow("Total", formatPrice(total), true)
        }
    }
}

@Composable
private fun CheckoutRow(
    label: String,
    value: String,
    emphasized: Boolean = false
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = if (emphasized) CardWhite else CardWhite.copy(alpha = 0.72f),
            fontWeight = if (emphasized) FontWeight.Bold else FontWeight.Normal
        )

        Text(
            text = value,
            color = if (emphasized) GoldLight else CardWhite,
            fontWeight = FontWeight.Bold
        )
    }
}