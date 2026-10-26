package com.moonglow.candles.data.remote

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONArray
import org.json.JSONObject

class SupabaseWishlistService {
    private val saveMutex = Mutex()

    suspend fun fetchWishlist(email: String): Result<Set<String>> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        require(email.equals(identity.email, ignoreCase = true)) { "Wishlist owner mismatch." }
        val json = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/wishlist_items?select=product_id&user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
        )
        val array = JSONArray(json)
        buildSet {
            for (i in 0 until array.length()) add(array.getJSONObject(i).getString("product_id"))
        }
    }

    suspend fun saveWishlist(email: String, ids: Set<String>): Result<Unit> = runCatching {
        saveMutex.withLock {
            val identity = AuthenticatedSupabaseHttp.identity()
            require(email.equals(identity.email, ignoreCase = true)) { "Wishlist owner mismatch." }
            AuthenticatedSupabaseHttp.request(
                "DELETE", "/rest/v1/wishlist_items?user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
            )
            ids.forEach { productId ->
                AuthenticatedSupabaseHttp.request("POST", "/rest/v1/wishlist_items", JSONObject().apply {
                    put("user_id", identity.userId)
                    put("user_email", identity.email)
                    put("product_id", productId)
                })
            }
        }
    }
}
