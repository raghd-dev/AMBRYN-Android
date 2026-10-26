package com.moonglow.candles.presentation.screens.workshops

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
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.PlayCircle
import androidx.compose.material.icons.outlined.Spa
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
import com.moonglow.candles.data.model.Workshop
import com.moonglow.candles.data.repository.MoonGlowRepository
import com.moonglow.candles.presentation.components.GlowButton
import com.moonglow.candles.presentation.components.SectionHeader

@Composable
fun WorkshopsScreen(repo: MoonGlowRepository) {
    val workshops by repo.workshops.collectAsState()
    var message by remember { mutableStateOf<String?>(null) }
    var selectedWorkshop by remember { mutableStateOf<Workshop?>(null) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Background),
        contentPadding = PaddingValues(bottom = 90.dp)
    ) {
        item { WorkshopHero() }
        item { WorkshopBenefits() }
        item { SectionHeader(title = "Workshops & Courses") }

        item {
            Text(
                text = "Book cozy studio sessions, join creative candle-making experiences, or learn online at your own pace.",
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

        items(workshops) { workshop ->
            WorkshopCard(
                workshop = workshop,
                onOpen = { selectedWorkshop = workshop },
                onBook = {
                    repo.bookWorkshop(workshop.id)
                        .onSuccess {
                            message =
                                if (workshop.type == "Course") "Course added to your learning list"
                                else "Seat reserved successfully"
                        }
                        .onFailure { message = it.message }
                }
            )
        }
    }

    selectedWorkshop?.let { workshop ->
        WorkshopDetailsDialog(
            workshop = workshop,
            onDismiss = { selectedWorkshop = null },
            onBook = {
                repo.bookWorkshop(workshop.id)
                    .onSuccess {
                        message =
                            if (workshop.type == "Course") "Course added to your learning list"
                            else "Seat reserved successfully"
                        selectedWorkshop = null
                    }
                    .onFailure { message = it.message }
            }
        )
    }
}

@Composable
private fun WorkshopHero() {
    Box(modifier = Modifier.fillMaxWidth().height(370.dp)) {
        Image(
            painter = painterResource(id = R.drawable.hero_workshop),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier.fillMaxSize().background(
                Brush.verticalGradient(
                    listOf(Color.Transparent, Charcoal.copy(.25f), Charcoal.copy(.9f))
                )
            )
        )

        Column(
            modifier = Modifier.align(Alignment.BottomStart).padding(24.dp)
        ) {
            Text(
                text = "Make Candles,\nMake Memories",
                style = MaterialTheme.typography.displayLarge,
                color = CardWhite,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(10.dp))

            Text(
                text = "Immersive studio workshops and premium online courses.",
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
                    text = "Studio sessions • Couples classes • Online courses",
                    color = GoldLight,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 9.dp)
                )
            }
        }
    }
}

@Composable
private fun WorkshopBenefits() {
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
            WorkshopBenefit(Icons.Outlined.Spa, "Hands-on")
            WorkshopBenefit(Icons.Outlined.Groups, "Small Groups")
            WorkshopBenefit(Icons.Outlined.PlayCircle, "Courses")
            WorkshopBenefit(Icons.Outlined.WorkspacePremium, "Premium")
        }
    }
}

@Composable
private fun WorkshopBenefit(icon: ImageVector, label: String) {
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
private fun WorkshopCard(
    workshop: Workshop,
    onOpen: () -> Unit,
    onBook: () -> Unit
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
                    painter = painterResource(id = workshop.imageRes),
                    contentDescription = workshop.title,
                    modifier = Modifier.fillMaxWidth().height(230.dp),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier.fillMaxWidth().height(230.dp).background(
                        Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(.28f)))
                    )
                )

                Surface(
                    modifier = Modifier.align(Alignment.TopStart).padding(14.dp),
                    color = CardWhite.copy(.92f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = workshop.type,
                        color = GoldDark,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }

                Surface(
                    modifier = Modifier.align(Alignment.TopEnd).padding(14.dp),
                    color = Charcoal.copy(.78f),
                    shape = RoundedCornerShape(100.dp)
                ) {
                    Text(
                        text = com.moonglow.candles.presentation.components.formatPrice(workshop.price),
                        color = CardWhite,
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                        style = MaterialTheme.typography.labelLarge
                    )
                }
            }

            Column(modifier = Modifier.padding(20.dp)) {
                Text(
                    text = workshop.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Charcoal
                )

                Spacer(Modifier.height(10.dp))

                Text(workshop.description, color = SoftBrown)

                Spacer(Modifier.height(12.dp))

                Text(
                    text = "${workshop.duration} • ${workshop.dateLabel}",
                    color = GoldDark,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = if (workshop.type == "Course") "Online access available" else "${workshop.seatsLeft} seats left",
                    color = SoftBrown
                )

                Spacer(Modifier.height(18.dp))

                GlowButton(
                    text = if (workshop.type == "Course") "Start Course" else "Reserve Seat",
                    modifier = Modifier.fillMaxWidth()
                ) {
                    onBook()
                }
            }
        }
    }
}

@Composable
private fun WorkshopDetailsDialog(
    workshop: Workshop,
    onDismiss: () -> Unit,
    onBook: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            GlowButton(
                text = if (workshop.type == "Course") "Start Course" else "Reserve Seat",
                modifier = Modifier.fillMaxWidth()
            ) { onBook() }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Close") }
        },
        title = {
            Text(workshop.title, style = MaterialTheme.typography.headlineMedium)
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Image(
                    painter = painterResource(id = workshop.imageRes),
                    contentDescription = workshop.title,
                    modifier = Modifier.fillMaxWidth().height(220.dp),
                    contentScale = ContentScale.Crop
                )

                Text(workshop.description, color = SoftBrown)
                Text("Type: ${workshop.type}", color = GoldDark, fontWeight = FontWeight.Bold)
                Text("Date: ${workshop.dateLabel}")
                Text("Duration: ${workshop.duration}")
                Text("Price: ${formatPrice(workshop.price)}", color = GoldDark, fontWeight = FontWeight.Bold)

                Text(
                    text = if (workshop.type == "Course") "Online access available" else "${workshop.seatsLeft} seats left",
                    color = SoftBrown
                )
            }
        }
    )
}