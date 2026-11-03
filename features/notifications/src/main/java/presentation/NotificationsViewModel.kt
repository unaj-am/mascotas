
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
import kotlinx.coroutines.flow.catch
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
        _notifications.value = emptyList()

        notificationsJob = viewModelScope.launch {
            notificationRepository
                .observeNotifications(userId)
                .catch { error ->
                    Log.e(
                        TAG,
                        "Error al observar las notificaciones del usuario $userId",
                        error
                    )

                    _notifications.value = emptyList()
                }
                .collect { notifications ->
                    _notifications.value = notifications
                }
        }
    }

    fun stopObservingNotifications() {
        notificationsJob?.cancel()
        notificationsJob = null
        _notifications.value = emptyList()
    }

    fun markAsRead(
        userId: String,
        notificationId: String
    ) {
        viewModelScope.launch {
            try {
                notificationRepository.markAsRead(
                    userId = userId,
                    notificationId = notificationId
                )
            } catch (error: Exception) {
                Log.e(
                    TAG,
                    "Error al marcar la notificación como leída",
                    error
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
                } catch (error: Exception) {
                    Log.e(
                        TAG,
                        "Error al limpiar la notificación ${notification.id}",
                        error
                    )
                }
            }
        }
    }

    override fun onCleared() {
        stopObservingNotifications()
        super.onCleared()
    }

    private companion object {
        const val TAG = "NotificationsViewModel"
    }
}
