package com.moonglow.candles.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class ChatMessage(
    val id: Long,
    val sender: String,
    val email: String,
    val message: String,
    val createdAt: String
)

class SupabaseChatService {
    suspend fun sendMessage(sender: String, email: String, message: String): Result<Unit> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        require(email.equals(identity.email, ignoreCase = true)) { "Sender does not match login." }
        require(message.isNotBlank()) { "Message cannot be empty." }
        AuthenticatedSupabaseHttp.request("POST", "/rest/v1/chat_messages", JSONObject().apply {
            put("user_id", identity.userId)
            put("sender", sender.take(100))
            put("email", identity.email)
            put("message", message.trim().take(4000))
        })
        Unit
    }

    suspend fun fetchMessages(admin: Boolean = false): Result<List<ChatMessage>> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        val filter = if (admin) "" else "&user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
        val response = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/chat_messages?select=*&order=created_at.asc$filter"
        )
        val array = JSONArray(response)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(ChatMessage(
                    id = obj.optLong("id"), sender = obj.optString("sender"),
                    email = obj.optString("email"), message = obj.optString("message"),
                    createdAt = obj.optString("created_at")
                ))
            }
        }
    }
}
