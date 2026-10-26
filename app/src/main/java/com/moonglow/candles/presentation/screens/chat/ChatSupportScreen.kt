package com.moonglow.candles.presentation.screens.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.moonglow.candles.core.theme.*
import com.moonglow.candles.data.remote.ChatMessage
import com.moonglow.candles.data.remote.SupabaseChatService
import com.moonglow.candles.data.repository.MoonGlowRepository
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun ChatSupportScreen(repo: MoonGlowRepository) {
    val user by repo.user.collectAsState()
    val service = remember { SupabaseChatService() }
    val scope = rememberCoroutineScope()

    var messages by remember { mutableStateOf<List<ChatMessage>>(emptyList()) }
    var input by remember { mutableStateOf("") }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }

    fun loadMessages() {
        scope.launch {
            loading = true
            service.fetchMessages()
                .onSuccess {
                    messages = it
                    loading = false
                }
                .onFailure {
                    error = it.message
                    loading = false
                }
        }
    }
    LaunchedEffect(Unit) {
        while (true) {
            loadMessages()
            delay(6000)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .padding(16.dp)
    ) {
        Text(
            text = "Live Support",
            style = MaterialTheme.typography.headlineMedium,
            color = Charcoal,
            fontWeight = FontWeight.Bold
        )

        Spacer(Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator(color = Gold)
        }

        error?.let {
            Text(it, color = MaterialTheme.colorScheme.error)
        }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(msg)
            }
        }

        Row {
            OutlinedTextField(
                value = input,
                onValueChange = { input = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type your message...") },
                shape = RoundedCornerShape(18.dp)
            )

            Spacer(Modifier.width(8.dp))

            IconButton(
                onClick = {
                    val text = input.trim()
                    if (text.isNotBlank()) {
                        input = ""
                        scope.launch {
                            service.sendMessage(
                                sender = user?.name ?: "Guest",
                                email = user?.email ?: "guest@example.com",
                                message = text
                            )
                            loadMessages()
                        }
                    }
                }
            ) {
                Icon(Icons.Outlined.Send, null, tint = GoldDark)
            }
        }
    }
}

@Composable
private fun ChatBubble(msg: ChatMessage) {

    val isSupport =
        msg.sender.contains(
            "support",
            ignoreCase = true
        )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement =
            if (isSupport)
                Arrangement.Start
            else
                Arrangement.End
    ) {

        Card(
            shape = RoundedCornerShape(22.dp),

            colors = CardDefaults.cardColors(
                containerColor =
                    if (isSupport)
                        CreamDark
                    else
                        GoldLight.copy(alpha = 0.35f)
            )
        ) {

            Column(
                modifier = Modifier.padding(14.dp)
            ) {

                Text(
                    text =
                        if (isSupport)
                            "AMBRYN Support"
                        else
                            msg.sender,

                    fontWeight = FontWeight.Bold,

                    color =
                        if (isSupport)
                            GoldDark
                        else
                            Charcoal
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text = msg.message,
                    color = Charcoal
                )
            }
        }
    }
}
