package com.maxton.bad_android.core.network

import android.content.SharedPreferences

class AuthTokenStore(private val prefs: SharedPreferences) {

    fun getAccessToken(): String? =
        prefs.getString(KEY_ACCESS_TOKEN, null)

    fun getRefreshToken(): String? =
        prefs.getString(KEY_REFRESH_TOKEN, null)

    fun saveTokens(access: String, refresh: String) {
        prefs.edit()
            .putString(KEY_ACCESS_TOKEN, access)
            .putString(KEY_REFRESH_TOKEN, refresh)
            .apply()
    }

    fun clear() {
        prefs.edit()
            .remove(KEY_ACCESS_TOKEN)
            .remove(KEY_REFRESH_TOKEN)
            .apply()
    }

    fun hasTokens(): Boolean =
        !getAccessToken().isNullOrBlank()

    companion object {
        const val PREFS_NAME = "bad_prefs"
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
    }
}
