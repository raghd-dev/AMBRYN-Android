package com.moonglow.candles.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class StockItem(
    val productId: String,
    val productName: String,
    val stock: Int,
    val lowStockLimit: Int
)

/** Admin operations require a valid JWT AND server-side RLS admin authorization. */
class SupabaseAdminService {
    suspend fun fetchOrders(): Result<List<CloudOrder>> = SupabaseOrderHistoryService().fetchOrders(admin = true)
    suspend fun fetchMessages(): Result<List<ChatMessage>> = SupabaseChatService().fetchMessages(admin = true)

    suspend fun fetchStock(): Result<List<StockItem>> = runCatching {
        val response = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/product_stock?select=*&order=stock.asc"
        )
        val array = JSONArray(response)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(StockItem(obj.optString("product_id"), obj.optString("product_name"),
                    obj.optInt("stock"), obj.optInt("low_stock_limit")))
            }
        }
    }

    suspend fun updateStock(productId: String, stock: Int): Result<Unit> = runCatching {
        require(stock >= 0) { "Stock must be nonnegative." }
        val updated = AuthenticatedSupabaseHttp.request(
            "PATCH", "/rest/v1/product_stock?product_id=eq.${AuthenticatedSupabaseHttp.encode(productId)}",
            JSONObject().put("stock", stock),
            returnRepresentation = true
        )
        check(JSONArray(updated).length() == 1) {
            "Stock update was not applied. Confirm administrator permissions and product ID."
        }
        Unit
    }

    suspend fun updateOrderStatus(orderId: String, status: String): Result<Unit> = runCatching {
        require(status in setOf("Processing", "Packed", "Shipped", "Out for delivery", "Delivered", "Cancelled")) {
            "Invalid status."
        }
        val updated = AuthenticatedSupabaseHttp.request(
            "PATCH", "/rest/v1/orders?id=eq.${AuthenticatedSupabaseHttp.encode(orderId)}",
            JSONObject().put("status", status),
            returnRepresentation = true
        )
        check(JSONArray(updated).length() == 1) {
            "Order status update was not applied. Confirm administrator permissions and order ID."
        }
        Unit
    }

    suspend fun sendNotification(title: String, message: String, type: String): Result<Unit> = runCatching {
        AuthenticatedSupabaseHttp.request("POST", "/rest/v1/app_notifications", JSONObject().apply {
            put("title", title)
            put("message", message)
            put("type", type)
        })
        Unit
    }

    suspend fun replyToSupport(adminName: String, message: String): Result<Unit> =
        Result.failure(IllegalStateException(
            "Support replies are disabled until private conversation recipients are implemented."
        ))
}
