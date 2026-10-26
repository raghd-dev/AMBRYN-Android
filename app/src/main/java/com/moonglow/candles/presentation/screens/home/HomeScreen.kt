package com.moonglow.candles.presentation.screens.home

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moonglow.candles.R
import com.moonglow.candles.core.navigation.Route
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.repository.MoonGlowRepository  // ← ADD THIS LINE
import com.moonglow.candles.presentation.components.*
@Composable
fun HomeScreen(
    repo: MoonGlowRepository,
    nav: NavController
) {

    val wishlist by repo.wishlist.collectAsState()
    val liveReviews by repo.productReviews.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {

        // HERO SECTION
        item {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(640.dp)
            ) {

                Image(
                    painter = painterResource(id = R.drawable.hero_main),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Charcoal.copy(.22f),
                                    Charcoal.copy(.92f)
                                )
                            )
                        )
                )

                // TOP BAR
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 54.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Column {

                        Text(
                            text = "AMBRYN",
                            style = MaterialTheme.typography.headlineMedium,
                            color = CardWhite,
                            fontWeight = FontWeight.Bold
                        )

                        Text(
                            text = "LUXURY CANDLE HOUSE",
                            color = GoldLight,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }

                    Row {

                        IconButton(
                            onClick = {
                                nav.navigate(Route.Search.path)
                            }
                        ) {

                            Icon(
                                Icons.Outlined.Search,
                                contentDescription = null,
                                tint = CardWhite
                            )
                        }

                        IconButton(
                            onClick = {
                                nav.navigate(Route.Notifications.path)
                            }
                        ) {

                            Icon(
                                Icons.Outlined.NotificationsNone,
                                contentDescription = null,
                                tint = CardWhite
                            )
                        }
                    }
                }

                // HERO CONTENT
                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(26.dp)
                ) {

                    Text(
                        text = "Light That\nFeels Like Home",
                        style = MaterialTheme.typography.displayLarge,
                        color = CardWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    Text(
                        text = "Luxury handcrafted candles,\nDIY kits and immersive workshops\nfor softer slower living.",
                        color = CardWhite.copy(.88f),
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(26.dp))

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {

                        GlowButton(
                            text = "Shop Collection",
                            modifier = Modifier.width(190.dp)
                        ) {

                            nav.navigate(Route.Shop.path)
                        }

                        OutlinedButton(
                            onClick = {
                                nav.navigate(Route.Workshops.path)
                            },
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = CardWhite
                            ),
                            border = BorderStroke(
                                1.dp,
                                CardWhite.copy(.5f)
                            ),
                            shape = RoundedCornerShape(18.dp)
                        ) {

                            Text("Workshops")
                        }
                    }

                    Spacer(modifier = Modifier.height(34.dp))
                }
            }
        }

        // BENEFITS
        item {

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-28).dp),
                colors = CardDefaults.cardColors(
                    containerColor = CardWhite
                ),
                elevation = CardDefaults.cardElevation(8.dp),
                shape = RoundedCornerShape(28.dp)
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 22.dp),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {

                    BenefitItem("🌿", "Natural")
                    BenefitItem("🤍", "Handmade")
                    BenefitItem("🎁", "Gift Ready")
                    BenefitItem("🚚", "Fast Ship")
                }
            }
        }

        // FEATURED SECTION
        item {

            Spacer(modifier = Modifier.height(12.dp))

            SectionHeader(
                title = "Featured Candles",
                action = "View all"
            ) {

                nav.navigate(Route.Shop.path)
            }
        }

        item {

            LazyRow(
                contentPadding = PaddingValues(horizontal = 20.dp),
                horizontalArrangement = Arrangement.spacedBy(18.dp)
            ) {

                items(
                    repo.products.filter { it.isBestSeller }
                ) { product ->

                    ProductCard(
                        product = product,
                        isWishlisted = wishlist.contains(product.id),
                        modifier = Modifier.width(240.dp),
                        customerReviewCount = liveReviews[product.id]?.size,
                        customerRating = liveReviews[product.id]?.takeIf { it.isNotEmpty() }?.map { it.rating }?.average(),

                        onWishlist = {
                            repo.toggleWishlist(product.id)
                        },

                        onAdd = {
                            repo.addToCart(product)
                        },

                        onOpen = {
                            nav.navigate(Route.Shop.path)
                        }
                    )
                }
            }
        }

        // WORKSHOP BANNER
        item {

            Spacer(modifier = Modifier.height(40.dp))

            Box(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(30.dp))
            ) {

                Image(
                    painter = painterResource(id = R.drawable.workshop_banner),
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )

                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                listOf(
                                    Color.Transparent,
                                    Charcoal.copy(.75f)
                                )
                            )
                        )
                )

                Column(
                    modifier = Modifier
                        .align(Alignment.BottomStart)
                        .padding(22.dp)
                ) {

                    Text(
                        text = "Candle Workshops",
                        style = MaterialTheme.typography.headlineLarge,
                        color = CardWhite,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "Create your own signature scent\nwith guided luxury experiences.",
                        color = CardWhite.copy(.9f)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            nav.navigate(Route.Workshops.path)
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldDark
                        ),
                        shape = RoundedCornerShape(18.dp)
                    ) {

                        Text("Book Workshop")
                    }
                }
            }
        }

        item {

            Spacer(modifier = Modifier.height(90.dp))
        }
    }
}

@Composable
private fun BenefitItem(
    emoji: String,
    text: String
) {

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = emoji,
            style = MaterialTheme.typography.headlineSmall
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Charcoal
        )
    }
}