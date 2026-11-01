package com.app.core.data.notifications

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

object MascotasNotificationChannel {

    const val CHANNEL_ID = "mascotas_notifications"

    fun create(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notificaciones de Mascotas",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Solicitudes de paseos y novedades de tus mascotas"
            }

            val notificationManager =
                context.getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }
}