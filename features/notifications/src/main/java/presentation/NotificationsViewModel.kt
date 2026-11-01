
package com.app.features.notifications.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.AppNotification
import com.app.core.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val notificationRepository: NotificationRepository
) : ViewModel() {

    private val _notifications =
        MutableStateFlow<List<AppNotification>>(emptyList())

    val notifications: StateFlow<List<AppNotification>> = _notifications

    private var notificationsJob: Job? = null

    fun observeNotifications(userId: String) {
        notificationsJob?.cancel()

        notificationsJob = viewModelScope.launch {
            notificationRepository.observeNotifications(userId)
                .collect { notifications ->
                    _notifications.value = notifications
                }
        }
    }

    fun markAsRead(userId: String, notificationId: String) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(
                    userId = userId,
                    notificationId = notificationId
                )
            } catch (e: Exception) {
                Log.e(
                    "NotificationsViewModel",
                    "Error al marcar la notificación como leída",
                    e
                )
            }
        }
    }

    fun markAllAsRead(userId: String) {
        viewModelScope.launch {
            val pendingNotifications = _notifications.value
                .filter { !it.isRead }

            pendingNotifications.forEach { notification ->
                try {
                    notificationRepository.markAsRead(
                        userId = userId,
                        notificationId = notification.id
                    )
                } catch (e: Exception) {
                    Log.e(
                        "NotificationsViewModel",
                        "Error al limpiar la notificación ${notification.id}",
                        e
                    )
                }
            }
        }
    }
}
