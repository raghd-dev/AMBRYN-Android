package com.moonglow.candles.domain.checkout

import com.moonglow.candles.data.model.CartItem

/**
 * Pricing rules for AMBRYN's educational demo only (NOT a payment authority).
 * The threshold is intentionally strictly greater than $50, preserving the
 * previously tested UI/server-persistence behavior.
 */
data class DemoTotals(val subtotal: Double, val shipping: Double) {
    val total: Double get() = subtotal + shipping
}

fun calculateDemoTotals(cart: List<CartItem>): DemoTotals {
    val subtotal = cart.sumOf { it.product.price * it.quantity }
    val shipping = when {
        cart.isEmpty() || subtotal <= 0.0 -> 0.0
        subtotal > 50.0 -> 0.0
        else -> 5.99
    }
    return DemoTotals(subtotal = subtotal, shipping = shipping)
}

/**
 * Local form validation only. The phone field is not persisted by the current
 * demo order service; see README.md and docs/FINAL_RELEASE_GATES.md.
 */
fun validateDemoDelivery(name: String, phone: String, address: String): String? = when {
    name.isBlank() -> "Please enter your full name."
    phone.isBlank() -> "Please enter your phone number."
    address.isBlank() -> "Please enter your delivery address."
    else -> null
}
