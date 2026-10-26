package com.moonglow.candles.presentation.screens.search

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.LocalMall
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.ShoppingCart
import androidx.compose.material.icons.outlined.Spa
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
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.data.model.Kit
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.data.model.Workshop
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.EmptyState
import com.moonglow.candles.presentation.components.GlowButton

private enum class SearchType {
    PRODUCT,
    KIT,
    WORKSHOP
}

private data class SearchResult(
    val id: String,
    val title: String,
    val subtitle: String,
    val price: Double,
    val imageRes: Int,
    val type: SearchType,
    val product: Product? = null,
    val kit: Kit? = null,
    val workshop: Workshop? = null
)

@Composable
fun SearchScreen(
    repo: MoonGlowRepository
) {
    var query by remember { mutableStateOf("") }
    var selected by remember { mutableStateOf<SearchResult?>(null) }

    val workshops by repo.workshops.collectAsState()

    val productResults = repo.products.map {
        SearchResult(
            id = it.id,
            title = it.name,
            subtitle = "${it.category} • ${it.description}",
            price = it.price,
            imageRes = it.imageRes,
            type = SearchType.PRODUCT,
            product = it
        )
    }

    val kitResults = repo.kits.map {
        SearchResult(
            id = it.id,
            title = it.name,
            subtitle = "${it.level} kit • ${it.description}",
            price = it.price,
            imageRes = it.imageRes,
            type = SearchType.KIT,
            kit = it
        )
    }

    val workshopResults = workshops.map {
        SearchResult(
            id = it.id,
            title = it.title,
            subtitle = "${it.type} • ${it.duration} • ${it.dateLabel}",
            price = it.price,
            imageRes = it.imageRes,
            type = SearchType.WORKSHOP,
            workshop = it
        )
    }

    val allResults = productResults + kitResults + workshopResults

    val filteredResults =
        if (query.isBlank()) {
            allResults
        } else {
            allResults.filter {
                it.title.contains(query, ignoreCase = true) ||
                        it.subtitle.contains(query, ignoreCase = true)
            }
        }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item {
            SearchHeader(
                query = query,
                onQueryChange = { query = it }
            )
        }

        item {
            Text(
                text = if (query.isBlank()) {
                    "Explore candles, kits and workshops"
                } else {
                    "${filteredResults.size} result${if (filteredResults.size == 1) "" else "s"} found"
                },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
                color = SoftBrown,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        if (filteredResults.isEmpty()) {
            item {
                EmptyState(
                    icon = "🔎",
                    title = "No results found",
                    message = "Try searching for vanilla, kit, workshop, amber, rose, or course."
                )
            }
        } else {
            items(filteredResults) { result ->
                SearchResultCard(
                    result = result,
                    onOpen = {
                        selected = result
                    },
                    onQuickAction = {
                        when (result.type) {
                            SearchType.PRODUCT -> {
                                result.product?.let { repo.addToCart(it) }
                            }

                            SearchType.KIT -> {
                                result.kit?.let { repo.addKitToCart(it) }
                            }

                            SearchType.WORKSHOP -> {
                                result.workshop?.let { repo.bookWorkshop(it.id) }
                            }
                        }
                    }
                )
            }
        }
    }

    selected?.let { result ->
        SearchDetailsDialog(
            result = result,
            onDismiss = {
                selected = null
            },
            onAction = {
                when (result.type) {
                    SearchType.PRODUCT -> {
                        result.product?.let { repo.addToCart(it) }
                    }

                    SearchType.KIT -> {
                        result.kit?.let { repo.addKitToCart(it) }
                    }

                    SearchType.WORKSHOP -> {
                        result.workshop?.let { repo.bookWorkshop(it.id) }
                    }
                }

                selected = null
            }
        )
    }
}

@Composable
private fun SearchHeader(
    query: String,
    onQueryChange: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(horizontal = 20.dp, vertical = 28.dp)
    ) {
        Text(
            text = "Search",
            style = MaterialTheme.typography.displayLarge,
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Find candles, DIY kits, classes and courses.",
            color = SoftBrown,
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(modifier = Modifier.height(18.dp))

        OutlinedTextField(
            value = query,
            onValueChange = onQueryChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text("Search AMBRYN...")
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = null,
                    tint = GoldDark
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(22.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Gold,
                unfocusedBorderColor = CreamDark,
                focusedContainerColor = CardWhite,
                unfocusedContainerColor = CardWhite
            )
        )
    }
}

@Composable
private fun SearchResultCard(
    result: SearchResult,
    onOpen: () -> Unit,
    onQuickAction: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 9.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(26.dp),
        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        ),
        elevation = CardDefaults.cardElevation(7.dp)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(id = result.imageRes),
                contentDescription = result.title,
                modifier = Modifier
                    .size(92.dp)
                    .clip(RoundedCornerShape(22.dp)),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ResultTypeIcon(result.type)

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = result.type.name.lowercase().replaceFirstChar { it.uppercase() },
                        color = GoldDark,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold
                    )
                }

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = result.title,
                    color = Charcoal,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = result.subtitle,
                    color = SoftBrown,
                    style = MaterialTheme.typography.bodyMedium,
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = formatPrice(result.price),
                    color = GoldDark,
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            IconButton(
                onClick = onQuickAction,
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        GoldLight.copy(alpha = 0.22f),
                        CircleShape
                    )
            ) {
                Icon(
                    imageVector = when (result.type) {
                        SearchType.PRODUCT -> Icons.Outlined.ShoppingCart
                        SearchType.KIT -> Icons.Outlined.Inventory2
                        SearchType.WORKSHOP -> Icons.Outlined.Event
                    },
                    contentDescription = null,
                    tint = GoldDark
                )
            }
        }
    }
}

@Composable
private fun ResultTypeIcon(
    type: SearchType
) {
    Icon(
        imageVector = when (type) {
            SearchType.PRODUCT -> Icons.Outlined.LocalMall
            SearchType.KIT -> Icons.Outlined.Inventory2
            SearchType.WORKSHOP -> Icons.Outlined.Spa
        },
        contentDescription = null,
        tint = GoldDark,
        modifier = Modifier.size(17.dp)
    )
}

@Composable
private fun SearchDetailsDialog(
    result: SearchResult,
    onDismiss: () -> Unit,
    onAction: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlowButton(
                text = when (result.type) {
                    SearchType.PRODUCT -> "Add to Cart"
                    SearchType.KIT -> "Add Kit to Cart"
                    SearchType.WORKSHOP -> "Reserve / Start"
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                onAction()
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        },
        title = {
            Text(
                text = result.title,
                style = MaterialTheme.typography.headlineMedium,
                color = Charcoal
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Image(
                    painter = painterResource(id = result.imageRes),
                    contentDescription = result.title,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(220.dp)
                        .clip(RoundedCornerShape(24.dp)),
                    contentScale = ContentScale.Crop
                )

                Text(
                    text = result.subtitle,
                    color = SoftBrown,
                    style = MaterialTheme.typography.bodyMedium
                )

                Text(
                    text = formatPrice(result.price),
                    color = GoldDark,
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleLarge
                )

                result.product?.let {
                    Text(
                        text = "Notes: ${it.scentNotes.joinToString()}",
                        color = SoftBrown
                    )

                    Text(
                        text = "Burn time: ${it.burnTime}",
                        color = SoftBrown
                    )
                }

                result.kit?.let {
                    Text(
                        text = "Includes:",
                        fontWeight = FontWeight.Bold,
                        color = Charcoal
                    )

                    it.includes.forEach { item ->
                        Text(
                            text = "• $item",
                            color = SoftBrown
                        )
                    }
                }

                result.workshop?.let {
                    Text(
                        text = "Date: ${it.dateLabel}",
                        color = SoftBrown
                    )

                    Text(
                        text = "Duration: ${it.duration}",
                        color = SoftBrown
                    )

                    Text(
                        text = if (it.type == "Course") {
                            "Online access available"
                        } else {
                            "${it.seatsLeft} seats left"
                        },
                        color = GoldDark,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    )
}