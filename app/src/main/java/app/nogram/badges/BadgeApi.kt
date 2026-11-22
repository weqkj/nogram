package app.nogram.badges

import app.nogram.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.*
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * Клиент бэкенда значков. Выдача/снятие защищена на СЕРВЕРЕ:
 * проверка ID в приложении ничего не защищает (APK можно изменить),
 * поэтому сервер требует подписанный токен владельца (см. backend/server.js).
 */
class BadgeApi(private val http: OkHttpClient = OkHttpClient()) {
    private val base = BuildConfig.BACKEND_URL
    private val json = "application/json".toMediaType()

    suspend fun badgesOf(userId: Long): List<Badge> = withContext(Dispatchers.IO) {
        runCatching {
            val body = http.newCall(Request.Builder().url("$base/badges/$userId").build()).execute().use { it.body!!.string() }
            Json.parseToJsonElement(body).jsonObject["badges"]!!.jsonArray
                .mapNotNull { Badge.byId(it.jsonPrimitive.content) }
        }.getOrDefault(emptyList())
    }

    suspend fun grant(adminToken: String, userId: Long, badge: Badge): Boolean = admin(adminToken, "grant", userId, badge)
    suspend fun revoke(adminToken: String, userId: Long, badge: Badge): Boolean = admin(adminToken, "revoke", userId, badge)

    private suspend fun admin(token: String, op: String, userId: Long, badge: Badge) = withContext(Dispatchers.IO) {
        val payload = buildJsonObject { put("userId", userId); put("badge", badge.id) }.toString()
        runCatching {
            http.newCall(
                Request.Builder().url("$base/admin/$op")
                    .header("Authorization", "Bearer $token")
                    .post(payload.toRequestBody(json)).build()
            ).execute().use { it.isSuccessful }
        }.getOrDefault(false)
    }
}
