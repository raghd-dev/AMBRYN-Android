package com.moonglow.candles.presentation.screens.admin

/** Friendly display-only reference. Backend requests always use the untouched full ID. */
internal fun shortAdminOrderReference(orderId: String): String {
    val cleanId = orderId.trim()
    if (cleanId.isEmpty()) return "#UNKNOWN"
    val withoutPrefix = cleanId.removePrefix("AM-")
    return "#" + withoutPrefix.take(8).uppercase()
}
