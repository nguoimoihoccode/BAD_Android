package com.maxton.bad_android.features.auth.data.dto

import com.maxton.bad_android.features.auth.domain.entities.DeviceSession
import com.maxton.bad_android.features.auth.domain.entities.User
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

@Serializable
data class UserDto(
    val id: String,
    @SerialName("short_id") val shortId: String,
    val email: String,
    val username: String,
    @SerialName("full_name") val fullName: String? = null,
    val phone: String? = null,
    @SerialName("avatar_url") val avatarUrl: String? = null,
) {
    fun toDomain() = User(
        id = id,
        shortId = shortId,
        email = email,
        username = username,
        fullName = fullName,
        phone = phone,
        avatarUrl = avatarUrl,
    )
}

@Serializable
data class AuthResponseDto(
    val access: String,
    val refresh: String,
    val user: UserDto,
)

@Serializable
data class LoginRequest(
    val email: String,
    val password: String,
)

@Serializable
data class RegisterRequest(
    val email: String,
    val username: String,
    val password: String,
    @SerialName("full_name") val fullName: String? = null,
)

@Serializable
data class LogoutRequest(
    val refresh: String,
)

@Serializable
data class DeviceSessionDto(
    val id: String,
    @SerialName("device_info") val deviceInfo: String? = null,
    val ip: String? = null,
    @SerialName("user_agent") val userAgent: String? = null,
    @SerialName("created_at") val createdAt: String,
    @SerialName("expires_at") val expiresAt: String,
    @SerialName("is_current") val isCurrent: Boolean = false,
) {
    fun toDomain() = DeviceSession(
        id = id,
        deviceInfo = deviceInfo,
        ip = ip,
        userAgent = userAgent,
        createdAt = parseIsoDate(createdAt),
        expiresAt = parseIsoDate(expiresAt),
        isCurrent = isCurrent,
    )
}

@Serializable
data class DeviceSessionListDto(
    val results: List<DeviceSessionDto> = emptyList(),
)

private fun parseIsoDate(value: String): Date {
    val patterns = listOf(
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss'Z'",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSSXXX",
        "yyyy-MM-dd'T'HH:mm:ssXXX",
        "yyyy-MM-dd'T'HH:mm:ss.SSSXXX",
    )
    for (pattern in patterns) {
        try {
            val sdf = SimpleDateFormat(pattern, Locale.US)
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val parsed = sdf.parse(value)
            if (parsed != null) return parsed
        } catch (_: Exception) {
            // try next pattern
        }
    }
    return Date()
}
