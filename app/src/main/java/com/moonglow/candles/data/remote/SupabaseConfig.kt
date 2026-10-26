package com.moonglow.candles.data.remote

import com.moonglow.candles.BuildConfig

object SupabaseConfig {
    val url: String
        get() = BuildConfig.SUPABASE_URL.trimEnd('/')

    val publishableKey: String
        get() = BuildConfig.SUPABASE_PUBLISHABLE_KEY

    fun requireConfigured() {
        require(url.isNotBlank()) { "SUPABASE_URL is not configured in local.properties." }
        require(publishableKey.isNotBlank()) {
            "SUPABASE_PUBLISHABLE_KEY is not configured in local.properties."
        }
    }
}
