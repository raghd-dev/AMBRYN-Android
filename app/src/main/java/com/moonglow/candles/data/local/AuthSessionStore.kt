package com.moonglow.candles.data.local

import android.content.Context
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec
import org.json.JSONObject

/**
 * Auth credentials are encrypted with a non-exportable Android Keystore key.
 * Do not back up the encrypted preferences: the key cannot be restored on another device.
 */
data class SupabaseSession(
    val accessToken: String,
    val refreshToken: String,
    val userId: String,
    val email: String,
    val expiresAt: Long
)

class AuthSessionStore(context: Context) {
    private val prefs = context.applicationContext.getSharedPreferences(
        "supabase_secure_session", Context.MODE_PRIVATE
    )
    private val keyAlias = "ambryn_supabase_auth_aes_v1"

    private fun key(): SecretKey {
        val keyStore = KeyStore.getInstance("AndroidKeyStore").apply { load(null) }
        (keyStore.getKey(keyAlias, null) as? SecretKey)?.let { return it }

        val generator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, "AndroidKeyStore")
        generator.init(
            KeyGenParameterSpec.Builder(
                keyAlias,
                KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
            )
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(256)
                .build()
        )
        return generator.generateKey()
    }

    fun save(session: SupabaseSession) {
        val json = JSONObject().apply {
            put("access_token", session.accessToken)
            put("refresh_token", session.refreshToken)
            put("user_id", session.userId)
            put("email", session.email)
            put("expires_at", session.expiresAt)
        }.toString()
        val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
            init(Cipher.ENCRYPT_MODE, key())
        }
        val ciphertext = cipher.doFinal(json.toByteArray(Charsets.UTF_8))
        val payload = cipher.iv + ciphertext
        val encoded = Base64.encodeToString(payload, Base64.NO_WRAP)
        check(prefs.edit().putString("session", encoded).commit()) {
            "Couldn't securely save your login session."
        }
    }

    fun read(): SupabaseSession? {
        val encoded = prefs.getString("session", null) ?: return null
        return try {
            val payload = Base64.decode(encoded, Base64.NO_WRAP)
            require(payload.size > 12) { "Invalid encrypted session." }
            val cipher = Cipher.getInstance("AES/GCM/NoPadding").apply {
                init(Cipher.DECRYPT_MODE, key(), GCMParameterSpec(128, payload.copyOfRange(0, 12)))
            }
            val decrypted = cipher.doFinal(payload.copyOfRange(12, payload.size))
            val obj = JSONObject(String(decrypted, Charsets.UTF_8))
            SupabaseSession(
                accessToken = obj.getString("access_token"),
                refreshToken = obj.getString("refresh_token"),
                userId = obj.getString("user_id"),
                email = obj.getString("email"),
                expiresAt = obj.getLong("expires_at")
            )
        } catch (_: Exception) {
            // Missing/invalid key after an OS restore, corrupt data, or legacy session.
            clear()
            null
        }
    }

    fun clear() {
        prefs.edit().remove("session").commit()
    }
}
