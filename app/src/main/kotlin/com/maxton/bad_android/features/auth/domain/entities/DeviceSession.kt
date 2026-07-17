package com.maxton.bad_android.features.auth.domain.entities

import java.util.Date

data class DeviceSession(
    val id: String,
    val deviceInfo: String?,
    val ip: String?,
    val userAgent: String?,
    val createdAt: Date,
    val expiresAt: Date,
    val isCurrent: Boolean
)
