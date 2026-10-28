package com.moonglow.candles.data.remote

import org.json.JSONArray
import org.json.JSONObject

data class ProductReview(
    val id: Long,
    val productId: String,
    val userName: String,
    val userEmail: String,
    val rating: Int,
    val reviewText: String,
    val createdAt: String
)

class SupabaseReviewService {
    suspend fun submitReview(productId: String, userEmail: String, userName: String,
                             rating: Int, reviewText: String): Result<Unit> = runCatching {
        require(rating in 1..5) { "Rating must be between 1 and 5." }
        val identity = AuthenticatedSupabaseHttp.identity()
        require(userEmail.equals(identity.email, ignoreCase = true)) { "Review owner mismatch." }
        AuthenticatedSupabaseHttp.request("POST", "/rest/v1/product_reviews", JSONObject().apply {
            put("user_id", identity.userId)
            put("product_id", productId)
            put("user_email", identity.email)
            put("user_name", userName)
            put("rating", rating)
            put("review_text", reviewText)
        })
        Unit
    }

    suspend fun getProductReviews(productId: String): Result<List<ProductReview>> = runCatching {
        val response = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/product_reviews?select=id,product_id,user_name,rating,review_text,created_at&product_id=eq.${AuthenticatedSupabaseHttp.encode(productId)}&order=created_at.desc",
            authenticated = true
        )
        val array = JSONArray(response)
        buildList {
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                add(ProductReview(
                    id = obj.getLong("id"), productId = obj.getString("product_id"),
                    userName = obj.optString("user_name"), userEmail = obj.optString("user_email"),
                    rating = obj.getInt("rating"), reviewText = obj.optString("review_text"),
                    createdAt = obj.optString("created_at")
                ))
            }
        }
    }
}
