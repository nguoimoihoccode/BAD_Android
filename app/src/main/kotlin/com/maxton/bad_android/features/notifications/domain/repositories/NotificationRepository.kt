package com.maxton.bad_android.features.notifications.domain.repositories

import com.maxton.bad_android.features.notifications.domain.entities.NotificationItem

interface NotificationRepository {
    suspend fun getNotifications(): Result<List<NotificationItem>>
    suspend fun markAsRead(notificationId: String): Result<Unit>
    suspend fun markAllAsRead(): Result<Unit>
}
