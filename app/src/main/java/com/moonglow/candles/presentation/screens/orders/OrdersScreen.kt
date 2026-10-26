package com.moonglow.candles.presentation.screens.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudDone
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Payments
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.data.model.Order
import com.moonglow.candles.data.remote.CloudOrder
import com.moonglow.candles.data.remote.SupabaseOrderHistoryService
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.EmptyState
import kotlinx.coroutines.launch

@Composable
fun OrdersScreen(repo: MoonGlowRepository) {
    val localOrders by repo.orders.collectAsState()
    val bookings by repo.bookings.collectAsState()

    val service = remember { SupabaseOrderHistoryService() }
    var cloudOrders by remember { mutableStateOf<List<CloudOrder>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }

    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        service.fetchOrders()
            .onSuccess {
                cloudOrders = it
                loading = false
            }
            .onFailure {
                loading = false
            }
    }

    if (localOrders.isEmpty() && bookings.isEmpty() && cloudOrders.isEmpty() && !loading) {
        EmptyState(
            "📦",
            "No orders yet",
            "Checkout items or reserve a workshop."
        )
        return
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {

        item {

            Column {

                Text(
                    text = "Order History",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {

                        loading = true

                        scope.launch {

                            service.fetchOrders()
                                .onSuccess {

                                    cloudOrders = it
                                    loading = false
                                }

                                .onFailure {

                                    loading = false
                                }
                        }
                    },

                    colors = ButtonDefaults.buttonColors(
                        containerColor = Gold
                    ),

                    shape = RoundedCornerShape(18.dp)
                ) {

                    Text("Refresh Orders")
                }
            }
        }

        if (loading) {
            item {
                CircularProgressIndicator(color = Gold)
            }
        }

        if (cloudOrders.isNotEmpty()) {
            item {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Outlined.CloudDone,
                        contentDescription = null,
                        tint = GoldDark
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "Online Orders",
                        fontWeight = FontWeight.Bold,
                        color = GoldDark
                    )
                }
            }

            items(cloudOrders) { order ->
                CloudOrderCard(order)
            }
        }

        if (localOrders.isNotEmpty()) {
            item {
                Text(
                    text = "Local Orders",
                    fontWeight = FontWeight.Bold,
                    color = SoftBrown
                )
            }

            items(localOrders) { order ->
                PremiumOrderCard(order)
            }
        }

        items(bookings) { booking ->
            Card(
                shape = RoundedCornerShape(26.dp),
                colors = CardDefaults.cardColors(containerColor = CardWhite)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Workshop Booking",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = booking.workshopTitle,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(text = booking.dateLabel)

                    Spacer(modifier = Modifier.height(10.dp))

                    AssistChip(
                        onClick = {},
                        label = { Text(booking.status) }
                    )
                }
            }
        }
    }
}

@Composable
private fun CloudOrderCard(order: CloudOrder) {
    var showTracking by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.id,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                    color = Charcoal
                )

                StatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(18.dp))

            OrderTimeline(order.status)

            Spacer(modifier = Modifier.height(18.dp))

            InfoRow(
                icon = Icons.Outlined.Payments,
                title = "Payment",
                value = order.paymentMethod
            )

            Spacer(modifier = Modifier.height(12.dp))

            InfoRow(
                icon = Icons.Outlined.LocationOn,
                title = "Address",
                value = order.address
            )

            Spacer(modifier = Modifier.height(18.dp))

            Divider()

            Spacer(modifier = Modifier.height(14.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Total",
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                Text(
                    text = formatPrice(order.total),
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(18.dp))

            Button(
                onClick = { showTracking = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Track Order")
            }
        }
    }

    if (showTracking) {
        TrackingDialog(
            status = order.status,
            onDismiss = { showTracking = false }
        )
    }
}

@Composable
private fun PremiumOrderCard(order: Order) {
    var showTracking by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(30.dp),
        elevation = CardDefaults.cardElevation(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite)
    ) {
        Column(modifier = Modifier.padding(22.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Order ${order.id}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = order.dateLabel,
                        color = SoftBrown
                    )
                }

                StatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(22.dp))

            OrderTimeline(order.status)

            Spacer(modifier = Modifier.height(22.dp))

            InfoRow(
                icon = Icons.Outlined.Payments,
                title = "Payment",
                value = order.paymentMethod
            )

            Spacer(modifier = Modifier.height(14.dp))

            InfoRow(
                icon = Icons.Outlined.LocationOn,
                title = "Address",
                value = order.address
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Items",
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Spacer(modifier = Modifier.height(10.dp))

            order.items.forEach {
                Text(
                    text = "• ${it.product.name} × ${it.quantity}",
                    color = SoftBrown
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            Spacer(modifier = Modifier.height(18.dp))

            Divider()

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                Text(
                    text = formatPrice(order.total),
                    style = MaterialTheme.typography.titleLarge,
                    color = GoldDark,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = { showTracking = true },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(containerColor = Gold),
                shape = RoundedCornerShape(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.LocalShipping,
                    contentDescription = null
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text("Track Order")
            }
        }
    }

    if (showTracking) {
        TrackingDialog(
            status = order.status,
            onDismiss = { showTracking = false }
        )
    }
}

@Composable
private fun TrackingDialog(
    status: String,
    onDismiss: () -> Unit
) {
    val trackingNumber = remember {
        "MG-${System.currentTimeMillis().toString().takeLast(8)}"
    }

    val courier = "AMBRYN Express"
    val estimatedDelivery = "2–4 business days"

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close", color = GoldDark)
            }
        },
        title = {
            Text(
                text = "Track Order",
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )
        },
        text = {
            Column {
                Text("Tracking Number", color = SoftBrown)
                Text(trackingNumber, fontWeight = FontWeight.Bold, color = Charcoal)

                Spacer(Modifier.height(10.dp))

                Text("Courier", color = SoftBrown)
                Text(courier, fontWeight = FontWeight.Bold, color = Charcoal)

                Spacer(Modifier.height(10.dp))

                Text("Estimated Delivery", color = SoftBrown)
                Text(estimatedDelivery, fontWeight = FontWeight.Bold, color = GoldDark)

                Spacer(Modifier.height(18.dp))

                TrackingStep("Order Placed", true)
                TrackingStep("Packed", status in listOf("Packed", "Shipped", "Out for delivery", "Delivered"))
                TrackingStep("Shipped", status in listOf("Shipped", "Out for delivery", "Delivered"))
                TrackingStep("Out for delivery", status in listOf("Out for delivery", "Delivered"))
                TrackingStep("Delivered", status == "Delivered")
            }
        },
        shape = RoundedCornerShape(28.dp),
        containerColor = CardWhite
    )
}
@Composable
private fun TrackingStep(
    title: String,
    done: Boolean
) {
    Row(
        modifier = Modifier.padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(18.dp)
                .clip(CircleShape)
                .background(
                    if (done) Gold else CreamDark
                )
        )

        Spacer(modifier = Modifier.width(14.dp))

        Text(
            text = title,
            color = if (done) Charcoal else SoftBrown,
            fontWeight = if (done) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun StatusBadge(status: String) {
    Surface(
        shape = RoundedCornerShape(100.dp),
        color = GoldLight.copy(alpha = 0.25f)
    ) {
        Text(
            text = status,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = GoldDark,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun OrderTimeline(status: String) {
    val steps = listOf(
        "Processing",
        "Packed",
        "Shipped",
        "Delivered"
    )

    val current = steps.indexOf(status).takeIf { it >= 0 } ?: 0

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        steps.forEachIndexed { index, step ->
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Box(
                    modifier = Modifier
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(
                            if (index <= current) {
                                Brush.verticalGradient(
                                    listOf(GoldLight, GoldDark)
                                )
                            } else {
                                Brush.verticalGradient(
                                    listOf(CreamDark, CreamDark)
                                )
                            }
                        )
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = step,
                    color = if (index <= current) GoldDark else SoftBrown,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
private fun InfoRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    value: String
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldDark
        )

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                color = SoftBrown
            )

            Text(
                text = value,
                fontWeight = FontWeight.SemiBold,
                color = Charcoal
            )
        }
    }
}