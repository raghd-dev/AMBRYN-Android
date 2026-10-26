package com.moonglow.candles.presentation.screens.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.data.remote.ChatMessage
import com.moonglow.candles.data.remote.CloudOrder
import com.moonglow.candles.data.remote.StockItem
import com.moonglow.candles.data.remote.SupabaseAdminService
import kotlinx.coroutines.launch
import androidx.compose.ui.Alignment

@Composable
fun AdminScreen() {
    val service = remember { SupabaseAdminService() }
    val scope = rememberCoroutineScope()

    var orders by remember { mutableStateOf<List<CloudOrder>>(emptyList()) }
    var stock by remember { mutableStateOf<List<StockItem>>(emptyList()) }
    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var isLoading by remember { mutableStateOf(false) }  // Added loading state

    var tab by remember { mutableStateOf(0) }
    var notice by remember { mutableStateOf<String?>(null) }

    fun refresh() {
        scope.launch {
            isLoading = true  // Added
            service.fetchOrders()
                .onSuccess { orders = it }
                .onFailure { notice = "Orders could not load: ${it.message}" }
            service.fetchStock()
                .onSuccess { stock = it }
                .onFailure { notice = "Inventory could not load: ${it.message}" }
            service.fetchMessages()
                .onSuccess { messages = it }
                .onFailure { notice = "Support messages could not load: ${it.message}" }
            isLoading = false
        }
    }

    LaunchedEffect(Unit) {
        refresh()
    }

    val revenue = orders.sumOf { it.total }
    val processing = orders.count { it.status == "Processing" }
    val delivered = orders.count { it.status == "Delivered" }
    val lowStock = stock.count { it.stock <= it.lowStockLimit }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "AMBRYN Admin",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )
                    Text(text = "Business dashboard", color = SoftBrown)
                }
                FilledTonalIconButton(
                    onClick = { refresh() },
                    enabled = !isLoading
                ) {
                    Icon(Icons.Outlined.Refresh, contentDescription = "Refresh dashboard")
                }
            }
        }

        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard("Revenue", formatPrice(revenue), Icons.Outlined.AttachMoney, Modifier.weight(1f))
                KpiCard("Orders", orders.size.toString(), Icons.Outlined.ReceiptLong, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard("Processing", processing.toString(), Icons.Outlined.HourglassTop, Modifier.weight(1f))
                KpiCard("Delivered", delivered.toString(), Icons.Outlined.CheckCircle, Modifier.weight(1f))
            }
        }
        item {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                KpiCard("Low stock", lowStock.toString(), Icons.Outlined.Warning, Modifier.weight(1f))
                KpiCard("Messages", messages.size.toString(), Icons.Outlined.SupportAgent, Modifier.weight(1f))
            }
        }

        // The overview follows the cards directly instead of being pushed below an
        // overflowing tab row and a large empty area.
        if (tab == 0) {
            item {
                OverviewPanel(orders = orders, stock = stock, messages = messages)
            }
        }

        item {
            Text(
                text = "Manage",
                style = MaterialTheme.typography.titleMedium,
                color = Charcoal,
                fontWeight = FontWeight.SemiBold
            )
            Row(
                modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                AdminChip("Overview", tab == 0) { tab = 0 }
                AdminChip("Orders", tab == 1) { tab = 1 }
                AdminChip("Inventory", tab == 2) { tab = 2 }
                AdminChip("Support", tab == 3) { tab = 3 }
                AdminChip("Marketing", tab == 4) { tab = 4 }
            }
        }

        if (isLoading) {
            item {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth(), color = Gold)
            }
        }
        notice?.let { message ->
            item {
                Card(colors = CardDefaults.cardColors(containerColor = CreamDark)) {
                    Text(
                        text = message,
                        modifier = Modifier.fillMaxWidth().padding(14.dp),
                        color = GoldDark,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        }

        when (tab) {
            1 -> {
                if (orders.isEmpty() && !isLoading) item { AdminEmptyMessage("No demo orders yet") }
                items(orders, key = { it.id }) { order ->
                    AdminOrderCard(order) { status ->
                        scope.launch {
                            service.updateOrderStatus(order.id, status)
                                .onSuccess {
                                    notice = "Order ${order.id} updated to $status"
                                    refresh()
                                }
                                .onFailure { notice = it.message }
                        }
                    }
                }
            }
            2 -> {
                if (stock.isEmpty() && !isLoading) item { AdminEmptyMessage("No inventory records available") }
                items(stock, key = { it.productId }) { item ->
                    StockCard(item) { newStock ->
                        scope.launch {
                            service.updateStock(item.productId, newStock)
                                .onSuccess {
                                    notice = "${item.productName} stock updated"
                                    refresh()
                                }
                                .onFailure { notice = it.message }
                        }
                    }
                }
            }
            3 -> {
                if (messages.isEmpty() && !isLoading) item { AdminEmptyMessage("No support messages yet") }
                items(messages) { msg -> SupportMessageCard(msg) }
            }
            4 -> item {
                MarketingPanel { title, message, type ->
                    scope.launch {
                        service.sendNotification(title, message, type)
                            .onSuccess { notice = "Notification sent" }
                            .onFailure { notice = it.message }
                    }
                }
            }
        }
        item { Spacer(Modifier.height(16.dp)) }
    }

}

@Composable
private fun AdminEmptyMessage(message: String) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        shape = RoundedCornerShape(18.dp)
    ) {
        Text(
            text = message,
            color = SoftBrown,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
private fun KpiCard(
    title: String,
    value: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.fillMaxWidth().padding(14.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = GoldDark
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Text(
                text = title,
                color = SoftBrown
            )
        }
    }
}

@Composable
private fun AdminChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(text)
        }
    )
}

@Composable
private fun OverviewPanel(
    orders: List<CloudOrder>,
    stock: List<StockItem>,
    messages: List<ChatMessage>
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = "Business Summary",
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Total orders: ${orders.size}",
                color = SoftBrown
            )

            Text(
                text = "Total revenue: ${formatPrice(orders.sumOf { it.total })}",
                color = SoftBrown
            )

            Text(
                text = "Low stock products: ${
                    stock.count {
                        it.stock <= it.lowStockLimit
                    }
                }",
                color = SoftBrown
            )

            Text(
                text = "Support messages: ${messages.size}",
                color = SoftBrown
            )
        }
    }
}

@Composable
private fun AdminOrderCard(
    order: CloudOrder,
    onStatus: (String) -> Unit
) {
    val statuses = listOf(
        "Processing",
        "Packed",
        "Shipped",
        "Out for delivery",
        "Delivered",
        "Cancelled"
    )

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = order.id,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Text(
                text = "Total: ${formatPrice(order.total)}",
                color = GoldDark
            )

            Text(
                text = "Payment: ${order.paymentMethod}",
                color = SoftBrown
            )

            Text(
                text = "Address: ${order.address}",
                color = SoftBrown
            )

            Text(
                text = "Status: ${order.status}",
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Spacer(modifier = Modifier.height(10.dp))

            statuses.forEach { status ->
                TextButton(
                    onClick = {
                        onStatus(status)
                    }
                ) {
                    Text("Set $status")
                }
            }
        }
    }
}

@Composable
private fun StockCard(
    item: StockItem,
    onUpdate: (Int) -> Unit
) {
    var stockText by remember {
        mutableStateOf(item.stock.toString())
    }

    val low =
        item.stock <= item.lowStockLimit

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = item.productName,
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            Text(
                text =
                    if (low)
                        "LOW STOCK"
                    else
                        "IN STOCK",
                color =
                    if (low)
                        Terracotta
                    else
                        GoldDark,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = stockText,
                onValueChange = {
                    stockText = it
                },
                label = {
                    Text("Stock quantity")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = {
                    onUpdate(
                        stockText.toIntOrNull()
                            ?: item.stock
                    )
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Update Stock")
            }
        }
    }
}

@Composable
private fun SupportMessageCard(msg: ChatMessage) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(3.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(msg.sender, fontWeight = FontWeight.Bold, color = GoldDark)
            Text(msg.email, color = SoftBrown, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Text(msg.message, color = Charcoal)
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                "View only • Replies are unavailable in this demo",
                color = SoftBrown,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun MarketingPanel(
    onSend: (String, String, String) -> Unit
) {
    var title by remember {
        mutableStateOf("")
    }

    var message by remember {
        mutableStateOf("")
    }

    var type by remember {
        mutableStateOf("offer")
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(CardWhite),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {

        Column(
            modifier = Modifier.padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            Text(
                text = "Send Cloud Notification",
                fontWeight = FontWeight.Bold,
                color = Charcoal
            )

            OutlinedTextField(
                value = title,
                onValueChange = {
                    title = it
                },
                label = {
                    Text("Title")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = message,
                onValueChange = {
                    message = it
                },
                label = {
                    Text("Message")
                },
                modifier = Modifier.fillMaxWidth()
            )

            OutlinedTextField(
                value = type,
                onValueChange = {
                    type = it
                },
                label = {
                    Text("Type: info / offer / shipping / support")
                },
                modifier = Modifier.fillMaxWidth()
            )

            Button(
                onClick = {
                    if (
                        title.isNotBlank() &&
                        message.isNotBlank()
                    ) {
                        onSend(
                            title,
                            message,
                            type
                        )
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Gold
                ),
                shape = RoundedCornerShape(18.dp)
            ) {
                Text("Send Notification")
            }
        }
    }
}