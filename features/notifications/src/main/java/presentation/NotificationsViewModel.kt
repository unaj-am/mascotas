
package com.app.features.notifications.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.AppNotification
import com.app.core.domain.repository.NotificationRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
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
    private var observedUserId: String? = null

    fun observeNotifications(userId: String) {
        if (userId.isBlank()) return

        // Evita reiniciar un listener que ya está activo.
        if (
            observedUserId == userId &&
            notificationsJob?.isActive == true
        ) {
            return
        }

        notificationsJob?.cancel()

        // Evita mostrar notificaciones de otra cuenta.
        if (observedUserId != userId) {
            _notifications.value = emptyList()
        }

        observedUserId = userId

        notificationsJob = viewModelScope.launch {
            notificationRepository
                .observeNotifications(userId)
                .catch { error ->
                    if (error is CancellationException) {
                        throw error
                    }

                    Log.e(
                        TAG,
                        "Error al observar notificaciones de $userId",
                        error
                    )

                    // Conserva las notificaciones ante un error.
                }
                .collect { newNotifications ->
                    _notifications.value = newNotifications
                }
        }
    }

    fun stopObservingNotifications() {
        notificationsJob?.cancel()
        notificationsJob = null
        observedUserId = null
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
            } catch (error: CancellationException) {
                throw error
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
                } catch (error: CancellationException) {
                    throw error
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
