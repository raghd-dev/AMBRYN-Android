package com.moonglow.candles.data.remote

import org.json.JSONArray

data class CloudOrder(
    val id: String,
    val customerName: String,
    val customerEmail: String,
    val address: String,
    val paymentMethod: String,
    val total: Double,
    val status: String,
    val createdAt: String
)

class SupabaseOrderHistoryService {
    suspend fun fetchOrders(admin: Boolean = false): Result<List<CloudOrder>> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        val userFilter = if (admin) "" else "&user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
        val response = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/orders?select=*&order=created_at.desc$userFilter"
        )
        val array = JSONArray(response)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(CloudOrder(
                    id = obj.optString("id"),
                    customerName = obj.optString("customer_name"),
                    customerEmail = obj.optString("customer_email"),
                    address = obj.optString("address"),
                    paymentMethod = obj.optString("payment_method"),
                    total = obj.optDouble("total"),
                    status = obj.optString("status"),
                    createdAt = obj.optString("created_at")
                ))
            }
        }
    }
}
