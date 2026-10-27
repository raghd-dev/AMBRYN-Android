package com.moonglow.candles.domain.cart

import com.moonglow.candles.data.model.CartItem

/**
 * One cart row per product, summing quantities if older persisted data contains
 * duplicate rows. Keeps the first product instance and its original list order.
 * Never modifies the database or changes the original list.
 */
fun consolidateCartItems(items: List<CartItem>): List<CartItem> {
    val consolidated = linkedMapOf<String, CartItem>()
    for (item in items) {
        if (item.quantity <= 0) continue
        val productId = item.product.id
        val previous = consolidated[productId]
        consolidated[productId] = if (previous == null) {
            item
        } else {
            previous.copy(
                quantity = (previous.quantity.toLong() + item.quantity.toLong())
                    .coerceAtMost(Int.MAX_VALUE.toLong()).toInt()
            )
        }
    }
    return consolidated.values.toList()
}
