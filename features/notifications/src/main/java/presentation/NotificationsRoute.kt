package com.app.features.notifications.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun NotificationsRoute(
    userId: String,
    viewModel: NotificationsViewModel = hiltViewModel()
) {
    val notifications = viewModel.notifications.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
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
        }
    )
}