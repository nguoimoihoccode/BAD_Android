package com.maxton.bad_android.core.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

/**
 * Local notification channel scaffolding.
 *
 * TODO(FCM): Add Firebase Cloud Messaging when google-services.json is present.
 * - Apply com.google.gms.google-services plugin in app/build.gradle
 * - Add firebase-messaging dependency
 * - Implement FirebaseMessagingService for remote push
 */
object NotificationHelper {
    const val CHANNEL_ID = "courtside_default"
    private const val CHANNEL_NAME = "CourtSide"
    private const val CHANNEL_DESCRIPTION = "Default notifications for CourtSide"

    fun createDefaultChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return

        val channel = NotificationChannel(
            CHANNEL_ID,
            CHANNEL_NAME,
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = CHANNEL_DESCRIPTION
        }

        val manager = context.getSystemService(NotificationManager::class.java)
        manager?.createNotificationChannel(channel)
    }
}
