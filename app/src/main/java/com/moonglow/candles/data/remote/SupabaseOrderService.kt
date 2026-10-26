package com.moonglow.candles.data.remote

import com.moonglow.candles.data.model.Order
import org.json.JSONObject

class SupabaseOrderService {
    suspend fun saveOrder(order: Order, customerName: String, customerEmail: String): Result<Unit> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        require(customerEmail.equals(identity.email, ignoreCase = true)) { "Order owner mismatch." }
        val body = JSONObject().apply {
            put("id", order.id)
            put("user_id", identity.userId)
            put("customer_name", customerName)
            put("customer_email", identity.email)
            put("address", order.address)
            put("payment_method", order.paymentMethod)
            put("total", order.total)
            put("status", order.status)
        }
        AuthenticatedSupabaseHttp.request("POST", "/rest/v1/orders", body)
        Unit
    }
}
