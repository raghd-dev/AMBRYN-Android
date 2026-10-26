package com.moonglow.candles.data.remote

import org.json.JSONArray

class SupabaseRoleService {
    suspend fun getRole(email: String): Result<String> = runCatching {
        val identity = AuthenticatedSupabaseHttp.identity()
        require(identity.email.equals(email.trim(), ignoreCase = true)) { "Role lookup must be for the signed-in user." }
        val json = AuthenticatedSupabaseHttp.request(
            "GET", "/rest/v1/user_roles?select=role&user_id=eq.${AuthenticatedSupabaseHttp.encode(identity.userId)}"
        )
        val array = JSONArray(json)
        if (array.length() > 0 && array.getJSONObject(0).optString("role") == "admin") "admin" else "customer"
    }
}
