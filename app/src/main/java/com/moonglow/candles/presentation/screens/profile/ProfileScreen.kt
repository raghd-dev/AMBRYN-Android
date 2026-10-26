package com.moonglow.candles.presentation.screens.profile

import androidx.navigation.NavGraph.Companion.findStartDestination

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.moonglow.candles.R
import com.moonglow.candles.core.navigation.Route
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.repository.MoonGlowRepository

@Composable
fun ProfileScreen(
    repo: MoonGlowRepository,
    nav: NavController,
    onLogout: () -> Unit
) {
    val user by repo.user.collectAsState()
    val userRole by repo.userRole.collectAsState()
    val orders by repo.orders.collectAsState()
    val bookings by repo.bookings.collectAsState()

    val isGuest = user == null
    val realPoints = (orders.size * 120) + (bookings.size * 80)

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
        contentPadding = PaddingValues(bottom = 110.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Cream, CreamDark)))
                    .padding(top = 54.dp, bottom = 34.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Profile Image - Your Logo
                    Box(
                        modifier = Modifier
                            .size(110.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.verticalGradient(listOf(GoldLight, GoldDark)),
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        if (user != null) {
                            // Show user initials if logged in
                            Text(
                                text = user?.name?.take(2)?.uppercase() ?: "MG",
                                style = MaterialTheme.typography.displayLarge,
                                color = CardWhite,
                                fontWeight = FontWeight.Bold
                            )
                        } else {
                            // Show logo for guest mode
                            Image(
                                painter = painterResource(id = R.drawable.brand_mark),
                                contentDescription = "AMBRYN Logo",
                                modifier = Modifier.size(70.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Text(
                        text = user?.name ?: "AMBRYN Guest",
                        style = MaterialTheme.typography.headlineLarge,
                        color = Charcoal,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = user?.email ?: "guest@example.com",
                        color = SoftBrown,
                        style = MaterialTheme.typography.bodyLarge
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Surface(
                        color = GoldLight.copy(alpha = 0.22f),
                        shape = RoundedCornerShape(100.dp),
                        border = BorderStroke(1.dp, GoldLight.copy(alpha = 0.4f))
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WorkspacePremium,
                                contentDescription = null,
                                tint = GoldDark
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = if (isGuest) {
                                    "Guest Mode"
                                } else {
                                    "${user?.memberTier ?: "Gold Member"} • $realPoints pts • ${userRole.uppercase()}"
                                },
                                color = GoldDark,
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    if (isGuest) {
                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = { nav.navigate(Route.Login.path) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(56.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Gold)
                        ) {
                            Icon(Icons.Outlined.Login, contentDescription = null)
                            Spacer(modifier = Modifier.width(10.dp))
                            Text("Login / Sign Up", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(16.dp)) }

        item {
            ProfileItem(
                title = "My Orders (${orders.size})",
                subtitle = "Track your candle purchases",
                icon = Icons.Outlined.ReceiptLong
            ) {
                nav.navigate(Route.Orders.path)
            }
        }

        item {
            ProfileItem(
                title = "Wishlist",
                subtitle = "Your saved candle collection",
                icon = Icons.Outlined.FavoriteBorder
            ) {
                nav.navigate(Route.Wishlist.path)
            }
        }

        item {
            ProfileItem(
                title = "Bookings & Courses (${bookings.size})",
                subtitle = "Manage workshops and classes",
                icon = Icons.Outlined.Event
            ) {
                nav.navigate(Route.Workshops.path)
            }
        }

        item {
            ProfileItem(
                title = "Notifications",
                subtitle = "AMBRYN updates and alerts",
                icon = Icons.Outlined.Notifications
            ) {
                nav.navigate(Route.Notifications.path)
            }
        }

        item {
            ProfileItem(
                title = "Live Support Chat",
                subtitle = "Talk with AMBRYN support",
                icon = Icons.Outlined.SupportAgent
            ) {
                nav.navigate(Route.ChatSupport.path)
            }
        }

        if (userRole == "admin") {
            item {
                ProfileItem(
                    title = "Admin Dashboard",
                    subtitle = "Manage products, orders, and support",
                    icon = Icons.Outlined.AdminPanelSettings
                ) {
                    nav.navigate(Route.Admin.path)
                }
            }
        }

        item {
            ProfileItem(
                title = "Settings",
                subtitle = "Customize your experience",
                icon = Icons.Outlined.Settings
            ) {
                nav.navigate(Route.Settings.path)
            }
        }

        if (!isGuest) {
            item {
                Spacer(modifier = Modifier.height(12.dp))

                Button(
                    onClick = {
                        repo.logout()
                        onLogout()
                        nav.navigate(Route.Login.path) {
                            popUpTo(nav.graph.findStartDestination().id) { inclusive = true }
                            launchSingleTop = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(58.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Terracotta,
                        contentColor = CardWhite
                    )
                ) {
                    Icon(Icons.Outlined.Logout, contentDescription = null)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text("Logout", fontWeight = FontWeight.Bold)
                }

                Spacer(modifier = Modifier.height(20.dp))
            }
        }
    }
}

@Composable
private fun ProfileItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(defaultElevation = 5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(50.dp)
                    .background(GoldLight.copy(alpha = 0.2f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = GoldDark)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = Charcoal,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = SoftBrown
                )
            }

            Icon(Icons.Outlined.ChevronRight, contentDescription = null, tint = GoldDark)
        }
    }
}