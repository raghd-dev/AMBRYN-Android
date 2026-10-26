package com.moonglow.candles.presentation.screens.notifications

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Campaign
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Notifications
import androidx.compose.material.icons.outlined.SupportAgent
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.remote.AppNotification
import com.moonglow.candles.data.remote.SupabaseNotificationService
import kotlinx.coroutines.delay

@Composable
fun NotificationsScreen() {

    val service = remember {
        SupabaseNotificationService()
    }

    var notifications by remember {
        mutableStateOf<List<AppNotification>>(emptyList())
    }

    var loading by remember {
        mutableStateOf(true)
    }

    LaunchedEffect(Unit) {

        while (true) {

            service.fetchNotifications()
                .onSuccess {

                    notifications = it
                    loading = false
                }

                .onFailure {

                    loading = false
                }

            delay(4000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(18.dp)
    ) {

        Text(
            text = "Notifications",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Charcoal
        )

        Spacer(modifier = Modifier.height(18.dp))

        if (loading) {

            CircularProgressIndicator(
                color = Gold
            )
        }

        LazyColumn(
            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            items(notifications) { notification ->

                NotificationCard(notification)
            }
        }
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification
) {

    val icon: ImageVector =
        when (notification.type) {

            "shipping" ->
                Icons.Outlined.LocalShipping

            "support" ->
                Icons.Outlined.SupportAgent

            "offer" ->
                Icons.Outlined.Campaign

            else ->
                Icons.Outlined.Notifications
        }

    Card(
        shape = RoundedCornerShape(24.dp),

        colors = CardDefaults.cardColors(
            containerColor = CardWhite
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp)
        ) {

            Surface(
                shape = RoundedCornerShape(18.dp),

                color = GoldLight.copy(alpha = 0.2f)
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,

                    tint = GoldDark,

                    modifier = Modifier.padding(14.dp)
                )
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column {

                Text(
                    text = notification.title,

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight = FontWeight.Bold,

                    color = Charcoal
                )

                Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = notification.message,

                    color = SoftBrown
                )
            }
        }
    }
}