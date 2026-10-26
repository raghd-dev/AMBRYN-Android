package com.moonglow.candles.data.remote

import org.json.JSONArray

data class AppNotification(
    val id: Long,
    val title: String,
    val message: String,
    val type: String,
    val isRead: Boolean,
    val createdAt: String
)

class SupabaseNotificationService {
    suspend fun fetchNotifications(): Result<List<AppNotification>> = runCatching {
        val response = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/app_notifications?select=*&order=created_at.desc"
        )
        val array = JSONArray(response)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(AppNotification(
                    id = obj.optLong("id"), title = obj.optString("title"),
                    message = obj.optString("message"), type = obj.optString("type"),
                    isRead = obj.optBoolean("is_read"), createdAt = obj.optString("created_at")
                ))
            }
        }
    }
}
