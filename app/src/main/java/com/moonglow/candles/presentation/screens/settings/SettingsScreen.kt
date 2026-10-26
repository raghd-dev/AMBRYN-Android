package com.moonglow.candles.presentation.screens.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*

@Composable
fun SettingsScreen() {
    var notifications by remember { mutableStateOf(true) }
    var darkMode by remember { mutableStateOf(false) }
    var emailOffers by remember { mutableStateOf(true) }
    var autoSaveCart by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
    ) {
        SettingsHeader()

        SettingsSwitchItem(
            title = "Push Notifications",
            subtitle = "Receive order, workshop and offer alerts",
            icon = Icons.Outlined.Notifications,
            checked = notifications,
            onCheckedChange = { notifications = it }
        )

        SettingsSwitchItem(
            title = "Email Offers",
            subtitle = "Get AMBRYN offers and candle tips",
            icon = Icons.Outlined.Email,
            checked = emailOffers,
            onCheckedChange = { emailOffers = it }
        )

        SettingsSwitchItem(
            title = "Save Cart",
            subtitle = "Keep your cart items for later",
            icon = Icons.Outlined.ShoppingCart,
            checked = autoSaveCart,
            onCheckedChange = { autoSaveCart = it }
        )

        SettingsSwitchItem(
            title = "Dark Mode",
            subtitle = "Coming soon with candle-night theme",
            icon = Icons.Outlined.DarkMode,
            checked = darkMode,
            onCheckedChange = { darkMode = it }
        )

        SettingsActionItem(
            title = "Privacy Policy",
            subtitle = "Read how AMBRYN protects your data",
            icon = Icons.Outlined.PrivacyTip
        )

        SettingsActionItem(
            title = "Help & Support",
            subtitle = "Contact support or report a problem",
            icon = Icons.Outlined.SupportAgent
        )

        SettingsActionItem(
            title = "About AMBRYN",
            subtitle = "Luxury handmade candles and creative workshops",
            icon = Icons.Outlined.Spa
        )
    }
}

@Composable
private fun SettingsHeader() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Cream)
            .padding(horizontal = 22.dp, vertical = 30.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.displayLarge,
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Customize your AMBRYN experience.",
            color = SoftBrown,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
private fun SettingsSwitchItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(icon)

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

            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldDark,
                    checkedTrackColor = GoldLight.copy(alpha = 0.55f)
                )
            )
        }
    }
}

@Composable
private fun SettingsActionItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp, vertical = 8.dp),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = CardWhite),
        elevation = CardDefaults.cardElevation(5.dp)
    ) {
        Row(
            modifier = Modifier.padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            SettingsIcon(icon)

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

            Icon(
                imageVector = Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = GoldDark
            )
        }
    }
}

@Composable
private fun SettingsIcon(
    icon: androidx.compose.ui.graphics.vector.ImageVector
) {
    Box(
        modifier = Modifier
            .size(50.dp)
            .background(
                GoldLight.copy(alpha = 0.2f),
                CircleShape
            ),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = GoldDark
        )
    }
}