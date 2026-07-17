package com.maxton.bad_android.features.notifications.domain.entities

import java.util.Date

data class NotificationItem(
    val id: String,
    val title: String,
    val body: String,
    val createdAt: Date,
    val isRead: Boolean,
    val type: String // 'booking', 'announcement', 'payment'
)
