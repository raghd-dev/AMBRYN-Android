package com.moonglow.candles.data.repository

import android.content.Context
import com.moonglow.candles.data.local.AuthSessionStore
import com.moonglow.candles.data.local.SupabaseSession
import com.moonglow.candles.data.remote.SupabaseConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Supabase Auth session storage, refresh, and restoration.
 * Database services use the validated JWT through AuthenticatedSupabaseHttp.
 */
class SupabaseRestAuthRepository(context: Context) {
    private val store = AuthSessionStore(context.applicationContext)

    suspend fun signup(name: String, email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val body = JSONObject().apply {
                    put("email", email.trim().lowercase())
                    put("password", password) // Never strip whitespace from passwords.
                    put("data", JSONObject().put("name", name.trim()))
                }
                val response = request("POST", "/auth/v1/signup", body)
                checkResponse(response)
                // Email confirmation may be enabled. Sign up never assumes a session.
                Unit
            }
        }

    suspend fun login(email: String, password: String): Result<Unit> =
        withContext(Dispatchers.IO) {
            runCatching {
                val response = request(
                    "POST", "/auth/v1/token?grant_type=password",
                    JSONObject().put("email", email.trim().lowercase()).put("password", password)
                )
                val session = sessionFrom(checkResponse(response))
                require(session.email.equals(email.trim(), ignoreCase = true)) {
                    "Signed-in account did not match the requested email."
                }
                store.save(session)
            }
        }

    /** Used at app startup; never trust an email stored in DataStore on its own. */
    suspend fun restoreSession(): SupabaseSession? = withContext(Dispatchers.IO) {
        var session = store.read() ?: return@withContext null
        try {
            val now = System.currentTimeMillis() / 1000L
            if (session.expiresAt <= now + 60L) {
                session = refresh(session)
            }
            val response = request("GET", "/auth/v1/user", bearerToken = session.accessToken)
            val user = checkResponse(response)
            if (user.getString("id") != session.userId ||
                !user.getString("email").equals(session.email, ignoreCase = true)
            ) {
                clearSession()
                return@withContext null
            }
            session
        } catch (_: Exception) {
            // Offline / timeout: do not clear a valid saved refresh token due to a
            // temporary network error; do not trust the profile as authenticated either.
            null
        }
    }

    /** For use by REST services after the matching RLS migration is ready. */
    suspend fun validAccessToken(): String? = withContext(Dispatchers.IO) {
        val session = store.read() ?: return@withContext null
        try {
            if (session.expiresAt > System.currentTimeMillis() / 1000L + 60L) {
                session.accessToken
            } else {
                refresh(session).accessToken
            }
        } catch (_: Exception) {
            null
        }
    }

    fun clearSession() = store.clear()

    private fun refresh(old: SupabaseSession): SupabaseSession {
        val response = request(
            "POST", "/auth/v1/token?grant_type=refresh_token",
            JSONObject().put("refresh_token", old.refreshToken)
        )
        val new = sessionFrom(checkResponse(response))
        require(new.userId == old.userId) { "Refresh returned a different account." }
        store.save(new)
        return new
    }

    private fun sessionFrom(obj: JSONObject): SupabaseSession {
        val user = obj.getJSONObject("user")
        val access = obj.getString("access_token")
        val refresh = obj.getString("refresh_token")
        require(access.isNotBlank() && refresh.isNotBlank()) { "Missing Supabase session tokens." }
        val seconds = obj.optLong("expires_in", 3600L).coerceAtLeast(1L)
        return SupabaseSession(
            accessToken = access,
            refreshToken = refresh,
            userId = user.getString("id"),
            email = user.getString("email"),
            expiresAt = System.currentTimeMillis() / 1000L + seconds
        )
    }

    private fun checkResponse(response: Pair<Int, String>): JSONObject {
        val parsed = runCatching { JSONObject(response.second) }.getOrNull()
        if (response.first !in 200..299) {
            val safeMessage = parsed?.optString("msg")?.takeIf { it.isNotBlank() }
                ?: parsed?.optString("error_description")?.takeIf { it.isNotBlank() }
                ?: parsed?.optString("message")?.takeIf { it.isNotBlank() }
                ?: "Supabase authentication error (HTTP ${response.first})."
            throw IllegalStateException(safeMessage)
        }
        return parsed ?: error("Unexpected Supabase authentication response.")
    }

    private fun request(
        method: String,
        endpoint: String,
        body: JSONObject? = null,
        bearerToken: String? = null
    ): Pair<Int, String> {
        SupabaseConfig.requireConfigured()
        val connection = URL("${SupabaseConfig.url}$endpoint")
            .openConnection() as HttpURLConnection
        try {
            connection.requestMethod = method
            connection.connectTimeout = 15000
            connection.readTimeout = 15000
            connection.setRequestProperty("apikey", SupabaseConfig.publishableKey)
            connection.setRequestProperty("Accept", "application/json")
            if (bearerToken != null) {
                connection.setRequestProperty("Authorization", "Bearer $bearerToken")
            }
            if (body != null) {
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.outputStream.bufferedWriter(Charsets.UTF_8).use { it.write(body.toString()) }
            }
            val status = connection.responseCode
            val text = (if (status in 200..299) connection.inputStream else connection.errorStream)
                ?.bufferedReader()?.use { it.readText() }.orEmpty()
            return status to text
        } finally {
            connection.disconnect()
        }
    }
}
