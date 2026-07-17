package com.maxton.bad_android.features.admin.domain.entities

import java.util.Date

data class MemberItem(
    val id: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val role: String, // 'admin', 'member'
    val skillLevel: String,
    val isBlocked: Boolean
)

data class JoinRequestItem(
    val id: String,
    val fullName: String,
    val username: String,
    val avatarUrl: String? = null,
    val skillLevel: String,
    val message: String,
    val requestedAt: Date
)
