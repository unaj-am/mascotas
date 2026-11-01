
package com.app.core.data.notifications

import android.Manifest
import android.app.NotificationManager
import android.content.pm.PackageManager
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MascotasFirebaseMessagingService : FirebaseMessagingService() {

    @Inject
    lateinit var fcmTokenDataSource: FcmTokenDataSource
    @Inject
    lateinit var firebaseAuth: FirebaseAuth

    override fun onMessageReceived(message: RemoteMessage) {
        super.onMessageReceived(message)

        val title = message.notification?.title
            ?: message.data["title"]
            ?: "Mascotas"

        val body = message.notification?.body
            ?: message.data["body"]
            ?: return

        val notification = NotificationCompat.Builder(
            this,
            MascotasNotificationChannel.CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(body)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()

        val hasPermission =
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.notify(
                System.currentTimeMillis().toInt(),
                notification
            )
        }
    }

    override fun onNewToken(token: String) {
        super.onNewToken(token)

        val userId = firebaseAuth.currentUser?.uid ?: return

        CoroutineScope(Dispatchers.IO).launch {
            try {
                fcmTokenDataSource.saveToken(
                    userId = userId,
                    token = token
                )
            } catch (_: Exception) {
                // Si falla, se volverá a intentar registrar
                // el token al iniciar una sesión válida.
            }
        }
    }
}
