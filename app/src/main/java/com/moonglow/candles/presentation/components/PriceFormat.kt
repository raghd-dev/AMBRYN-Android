package com.moonglow.candles.presentation.components

import java.util.Locale

/** Consistent US-dollar display for the fictional AMBRYN demo storefront. */
fun formatPrice(amount: Double): String = "$" + String.format(Locale.US, "%.2f", amount)
