package com.moonglow.candles.presentation.screens.kits

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.R
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.presentation.components.formatPrice
import com.moonglow.candles.data.model.Kit
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.GlowButton
import com.moonglow.candles.presentation.components.SectionHeader

@Composable
fun KitsScreen(repo: MoonGlowRepository) {
    var message by remember { mutableStateOf<String?>(null) }
    var selectedKit by remember { mutableStateOf<Kit?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item { KitsHero() }
        item { KitsBenefits() }
        item { SectionHeader(title = "DIY Candle Making Kits") }

        item {
            Text(
                text = "Create professional candles at home with premium soy wax, essential oils, tools, and guided steps.",
                modifier = Modifier.padding(horizontal = 20.dp),
                color = SoftBrown,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        message?.let {
            item {
                AssistChip(
                    onClick = { message = null },
                    label = { Text(it) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp)
                )
            }
        }

        items(repo.kits) { kit ->
            KitCard(
                kit = kit,
                onOpen = { selectedKit = kit },
                onAdd = {
                    repo.addKitToCart(kit)
                    message = "${kit.name} added to cart"
                }
            )
        }
    }

    selectedKit?.let { kit ->
        KitDetailsDialog(
            kit = kit,
            onDismiss = { selectedKit = null },
            onAdd = {
                repo.addKitToCart(kit)
                message = "${kit.name} added to cart"
                selectedKit = null
            }
        )
    }
}

@Composable
private fun KitsHero() {
    Box(modifier = Modifier.fillMaxWidth().height(360.dp)) {
        Image(
            painter = painterResource(id = R.drawable.hero_kits),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Charcoal.copy(.25f), Charcoal.copy(.88f))
                )
            )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        ) {
            Text(
                text = "Create Your Own Glow",
                style = MaterialTheme.typography.displayLarge,
                color = CardWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Premium DIY kits for calm, creative candle-making rituals at home.",
                color = CardWhite.copy(.9f),
                style = MaterialTheme.typography.bodyLarge
            )

            Spacer(Modifier.height(18.dp))

            Surface(
                color = Gold.copy(.18f),
                shape = RoundedCornerShape(100.dp),
                border = BorderStroke(1.dp, GoldLight.copy(.55f))
            ) {
                Text(
                    text = "Soy wax • Natural oils • Guided process",
                    color = GoldLight,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun KitsBenefits() {
    Card(
        modifier = Modifier.fillMaxWidth().padding(horizontal = 20.dp).offset(y = (-26).dp),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(10.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 22.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            KitBenefit(Icons.Outlined.Spa, "Natural")
            KitBenefit(Icons.Outlined.PlayCircle, "Guided")
            KitBenefit(Icons.Outlined.WorkspacePremium, "Premium")
            KitBenefit(Icons.Outlined.LocalShipping, "Gift Ready")
        }
    }
}

@Composable
private fun KitBenefit(icon: ImageVector, label: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier.size(42.dp).background(GoldLight.copy(.22f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = GoldDark, modifier = Modifier.size(22.dp))
        }

        Spacer(Modifier.height(8.dp))

        Text(label, color = Charcoal, style = MaterialTheme.typography.labelLarge)
    }
}

@Composable
private fun KitCard(
    kit: Kit,
    onOpen: () -> Unit,
    onAdd: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 12.dp)
            .clickable { onOpen() },
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 9.dp)
    ) {
        Column {
            Box {
                Image(
                    painter = painterResource(id = kit.imageRes),
                    contentDescription = kit.name,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier.fillMaxWidth().height(220.dp).background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.25f)))
                    )
                )

                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
                    color = CardWhite.copy(.92f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Outlined.Star, null, tint = GoldDark, modifier = Modifier.size(16.dp))
                        Spacer(Modifier.width(4.dp))
                        Text(kit.level, color = GoldDark, style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = kit.name,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Charcoal,
                        modifier = Modifier.weight(1f)
                    )

                    Text(
                        text = com.moonglow.candles.presentation.components.formatPrice(kit.price),
                        color = GoldDark,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(Modifier.height(10.dp))

                Text(kit.description, color = SoftBrown, style = MaterialTheme.typography.bodyMedium)

                Spacer(Modifier.height(14.dp))

                kit.includes.take(3).forEach {
                    Text("• $it", color = SoftBrown, style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(Modifier.height(18.dp))

                GlowButton("Add Kit to Cart", Modifier.fillMaxWidth()) { onAdd() }
            }
        }
    }
}

@Composable
private fun KitDetailsDialog(
    kit: Kit,
    onDismiss: () -> Unit,
    onAdd: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlowButton("Add Kit to Cart", Modifier.fillMaxWidth()) { onAdd() }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = {
            Text(kit.name, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(
                    painter = painterResource(id = kit.imageRes),
                    contentDescription = kit.name,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Text(kit.description, color = SoftBrown)
                Text("Level: ${kit.level}", color = GoldDark, fontWeight = FontWeight.Bold)
                Text("Price: ${formatPrice(kit.price)}", color = GoldDark, fontWeight = FontWeight.Bold)

                Text("What’s included:", fontWeight = FontWeight.Bold)

                kit.includes.forEach {
                    Text("• $it", color = SoftBrown)
                }
            }
        }
    )
}