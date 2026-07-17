package com.maxton.bad_android.core.network

import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import org.json.JSONObject
import java.util.concurrent.CompletableFuture

/**
 * OkHttp interceptor mirroring Flutter AuthInterceptor:
 * - Skips auth for login/register/refresh
 * - Attaches Bearer access token
 * - On 401: single-flight refresh, retry original request once
 * - On refresh failure: clear tokens + SessionExpiredBus.emit
 */
class AuthInterceptor(
    private val tokenStore: AuthTokenStore,
    private val refreshClient: OkHttpClient,
) : Interceptor {

    @Volatile
    private var refreshFuture: CompletableFuture<String?>? = null

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()
        val path = original.url.encodedPath

        if (isAuthPath(path)) {
            return chain.proceed(original)
        }

        val access = tokenStore.getAccessToken()
        val requestWithAuth = if (!access.isNullOrBlank()) {
            original.newBuilder()
                .header(HEADER_AUTHORIZATION, "Bearer $access")
                .build()
        } else {
            original
        }

        val response = chain.proceed(requestWithAuth)

        if (response.code != 401) {
            return response
        }

        val newAccess = refreshAccessToken()
        if (newAccess.isNullOrBlank()) {
            tokenStore.clear()
            SessionExpiredBus.emit()
            return response
        }

        response.close()

        val retried = original.newBuilder()
            .header(HEADER_AUTHORIZATION, "Bearer $newAccess")
            .build()
        return chain.proceed(retried)
    }

    private fun refreshAccessToken(): String? {
        val existing = refreshFuture
        if (existing != null) {
            return existing.join()
        }

        val created = CompletableFuture<String?>()
        synchronized(this) {
            val again = refreshFuture
            if (again != null) {
                return again.join()
            }
            refreshFuture = created
        }

        try {
            val refresh = tokenStore.getRefreshToken()
            if (refresh.isNullOrBlank()) {
                created.complete(null)
                return null
            }

            val bodyJson = JSONObject().put("refresh", refresh).toString()
            val body = bodyJson.toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(ApiConfig.BASE_URL + "auth/refresh")
                .post(body)
                .header("Content-Type", "application/json")
                .build()

            refreshClient.newCall(request).execute().use { refreshResponse ->
                if (!refreshResponse.isSuccessful) {
                    tokenStore.clear()
                    created.complete(null)
                    return null
                }

                val payload = refreshResponse.body?.string().orEmpty()
                val json = JSONObject(payload)
                val access = json.optString("access", "")
                val newRefresh = json.optString("refresh", "")
                if (access.isBlank() || newRefresh.isBlank()) {
                    tokenStore.clear()
                    created.complete(null)
                    return null
                }

                tokenStore.saveTokens(access, newRefresh)
                created.complete(access)
                return access
            }
        } catch (_: Exception) {
            tokenStore.clear()
            created.complete(null)
            return null
        } finally {
            synchronized(this) {
                if (refreshFuture === created) {
                    refreshFuture = null
                }
            }
        }
    }

    private fun isAuthPath(path: String): Boolean =
        path.contains("/auth/login") ||
            path.contains("/auth/register") ||
            path.contains("/auth/refresh")

    companion object {
        private const val HEADER_AUTHORIZATION = "Authorization"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
