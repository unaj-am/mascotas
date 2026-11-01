
package com.app.core.data.repository

import com.app.core.data.datasource.NotificationRemoteDataSource
import com.app.core.domain.model.AppNotification
import com.app.core.domain.repository.NotificationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotificationRepositoryImpl @Inject constructor(
    private val remoteDataSource: NotificationRemoteDataSource
) : NotificationRepository {

    override fun observeNotifications(
        userId: String
    ): Flow<List<AppNotification>> {
        return remoteDataSource.observeNotifications(userId)
    }

    override suspend fun markAsRead(
        userId: String,
        notificationId: String
    ) {
        remoteDataSource.markAsRead(
            userId = userId,
            notificationId = notificationId
        )
    }
}
