package com.moonglow.candles.data.remote

import com.moonglow.candles.data.model.CartItem
import com.moonglow.candles.data.model.Product
import com.moonglow.candles.domain.cart.consolidateCartItems
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject

class SupabaseCartService {
    private val saveMutex = Mutex()

    suspend fun saveCart(email: String, cart: List<CartItem>): Result<Unit> = runCatching {
        saveMutex.withLock {
            val identity = AuthenticatedSupabaseHttp.identity()
            require(email.equals(identity.email, ignoreCase = true)) { "Cart owner mismatch." }
            val path = "/rest/v1/cart_items?user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
            AuthenticatedSupabaseHttp.request("DELETE", path)
            // Minimal demo persistence. A future version should use one transactional RPC.
            consolidateCartItems(cart).forEach { item ->
                val body = JSONObject().apply {
                    put("user_id", identity.userId)
                    put("user_email", identity.email)
                    put("product_id", item.product.id)
                    put("product_name", item.product.name)
                    put("price", item.product.price)
                    put("quantity", item.quantity)
                }
                AuthenticatedSupabaseHttp.request("POST", "/rest/v1/cart_items", body)
            }
        }
    }

    suspend fun fetchCart(email: String, products: List<Product>): Result<List<CartItem>> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        require(email.equals(identity.email, ignoreCase = true)) { "Cart owner mismatch." }
        val result = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/cart_items?select=*&user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
        )
        val array = JSONArray(result)
        consolidateCartItems(buildList {
            for (i in 0 until array.length()) {
                val item = array.getJSONObject(i)
                val product = products.find { it.id == item.optString("product_id") }
                val quantity = item.optInt("quantity", 1)
                if (product != null && quantity > 0) add(CartItem(product, quantity))
            }
        })
    }
}
