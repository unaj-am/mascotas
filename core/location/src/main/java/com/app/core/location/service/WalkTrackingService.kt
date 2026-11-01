package com.app.core.location.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.app.core.location.usecase.TrackWalkLocationUseCase
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

@AndroidEntryPoint
class WalkTrackingService : Service() {

    @Inject
    lateinit var trackWalkLocationUseCase: TrackWalkLocationUseCase

    private val serviceScope = CoroutineScope(
        SupervisorJob() + Dispatchers.IO
    )

    private var trackingJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        when (intent?.action) {

            ACTION_STOP -> {
                stopTracking()
                return START_NOT_STICKY
            }

            ACTION_START -> {
                val walkId = intent.getStringExtra(EXTRA_WALK_ID)

                if (walkId.isNullOrBlank()) {
                    stopSelf()
                    return START_NOT_STICKY
                }

                startTracking(walkId)
            }

            else -> {
                stopSelf()
                return START_NOT_STICKY
            }
        }

        return START_NOT_STICKY
    }

    private fun startTracking(walkId: String) {
        if (trackingJob?.isActive == true) {
            return
        }

        startForegroundServiceNotification()

        trackingJob = serviceScope.launch {
            try {
                trackWalkLocationUseCase(walkId)
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                Log.e(
                    TAG,
                    "Error durante el seguimiento del paseo",
                    exception
                )

                stopTracking()
            }
        }
    }

    private fun startForegroundServiceNotification() {
        val notification = createNotification()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            startForeground(
                NOTIFICATION_ID,
                notification,
                ServiceInfo.FOREGROUND_SERVICE_TYPE_LOCATION
            )
        } else {
            startForeground(
                NOTIFICATION_ID,
                notification
            )
        }
    }

    private fun createNotification(): Notification {
        val stopIntent = Intent(
            this,
            WalkTrackingService::class.java
        ).apply {
            action = ACTION_STOP
        }

        val stopPendingIntent = PendingIntent.getService(
            this,
            0,
            stopIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or
                    PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(
            this,
            CHANNEL_ID
        )
            .setSmallIcon(android.R.drawable.ic_menu_mylocation)
            .setContentTitle("Paseo en curso")
            .setContentText("Compartiendo ubicación del paseador")
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .addAction(
                android.R.drawable.ic_media_pause,
                "Detener",
                stopPendingIntent
            )
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Seguimiento de paseos",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificaciones del seguimiento GPS"
            }

            val notificationManager =
                getSystemService(NotificationManager::class.java)

            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun stopTracking() {
        trackingJob?.cancel()
        trackingJob = null

        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    override fun onDestroy() {
        trackingJob?.cancel()
        serviceScope.cancel()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {

        private const val TAG = "WalkTrackingService"

        private const val CHANNEL_ID = "walk_tracking_channel"
        private const val NOTIFICATION_ID = 1001

        private const val ACTION_START =
            "com.app.core.location.action.START_TRACKING"

        private const val ACTION_STOP =
            "com.app.core.location.action.STOP_TRACKING"

        private const val EXTRA_WALK_ID = "walk_id"

        /**
         * Inicia el seguimiento de un paseo.
         */
        fun start(
            context: Context,
            walkId: String
        ) {
            require(walkId.isNotBlank()) {
                "El identificador del paseo es obligatorio"
            }

            val intent = Intent(
                context,
                WalkTrackingService::class.java
            ).apply {
                action = ACTION_START
                putExtra(EXTRA_WALK_ID, walkId)
            }

            ContextCompat.startForegroundService(
                context,
                intent
            )
        }

        /**
         * Detiene el seguimiento activo.
         */
        fun stop(context: Context) {
            val intent = Intent(
                context,
                WalkTrackingService::class.java
            ).apply {
                action = ACTION_STOP
            }

            context.startService(intent)
        }
    }
}