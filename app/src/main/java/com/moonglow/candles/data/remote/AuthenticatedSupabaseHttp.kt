package com.moonglow.candles.data.remote

import android.content.Context
import com.moonglow.candles.data.local.AuthSessionStore
import com.moonglow.candles.data.repository.SupabaseRestAuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.URLEncoder

/** Common REST layer: publishable key identifies the application; JWT identifies the user. */
object AuthenticatedSupabaseHttp {
    @Volatile private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    data class Identity(val userId: String, val email: String, val token: String)

    suspend fun identity(): Identity {
        val context = appContext ?: error("Auth services not initialized")
        val token = SupabaseRestAuthRepository(context).validAccessToken()
            ?: error("Session expired. Please log in again.")
        val session = AuthSessionStore(context).read()
            ?: error("You must log in first.")
        require(session.userId.isNotBlank()) { "Invalid user session." }
        return Identity(session.userId, session.email, token)
    }

    fun encode(value: String): String = URLEncoder.encode(value, "UTF-8")

    private val client = OkHttpClient.Builder().build()

    suspend fun request(
        method: String,
        path: String,
        body: JSONObject? = null,
        authenticated: Boolean = true,
        returnRepresentation: Boolean = false
    ): String = withContext(Dispatchers.IO) {
        SupabaseConfig.requireConfigured()
        val identity = if (authenticated) identity() else null
        val url = "${SupabaseConfig.url}$path"
        val builder = Request.Builder()
            .url(url)
            .header("apikey", SupabaseConfig.publishableKey)
            .header("Accept", "application/json")
        if (returnRepresentation) builder.header("Prefer", "return=representation")
        identity?.let { builder.header("Authorization", "Bearer ${it.token}") }
        val requestBody = body?.toString()?.toRequestBody("application/json".toMediaType())
        val request = when (method) {
            "GET" -> builder.get().build()
            "DELETE" -> builder.delete().build()
            "POST" -> builder.post(requireNotNull(requestBody)).build()
            "PATCH" -> builder.patch(requireNotNull(requestBody)).build()
            else -> error("Unsupported HTTP verb: $method")
        }
        client.newCall(request).execute().use { response ->
            val payload = response.body?.string().orEmpty()
            if (!response.isSuccessful) {
                throw IllegalStateException("Supabase HTTP ${response.code}: ${payload.take(500)}")
            }
            payload
        }
    }
}
