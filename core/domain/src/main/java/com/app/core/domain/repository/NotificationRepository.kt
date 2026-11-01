package com.app.core.domain.repository

import com.app.core.domain.model.AppNotification
import kotlinx.coroutines.flow.Flow

interface NotificationRepository {

    fun observeNotifications(
        userId: String
    ): Flow<List<AppNotification>>

    suspend fun markAsRead(
        userId: String,
        notificationId: String
    )
}