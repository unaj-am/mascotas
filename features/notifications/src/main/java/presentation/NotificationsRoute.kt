
package com.app.features.notifications.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel
import com.app.core.domain.model.AppNotification

@Composable
fun NotificationsRoute(
    userId: String,
    onBack: () -> Unit = {},
    onNotificationSelected: (AppNotification) -> Unit = {},
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications =
        viewModel.notifications.collectAsStateWithLifecycle()

    LaunchedEffect(userId, viewModel) {
        viewModel.observeNotifications(userId)
    }

    NotificationsScreen(
        notifications = notifications.value,

        onNotificationClick = { notification ->
            if (!notification.isRead) {
                viewModel.markAsRead(
                    userId = userId,
                    notificationId = notification.id
                )
            }
            onNotificationSelected(notification)
        },

        onBackClick = {
            onBack()
        },

        onClearAllClick = {
            viewModel.markAllAsRead(userId)
        }
    )
}
