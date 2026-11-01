package com.app.features.notifications.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.AppNotification
import androidx.compose.foundation.clickable
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.text.font.FontWeight

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification> = emptyList(),
    onNotificationClick: (AppNotification) -> Unit = {}
)  {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = "Notificaciones")

        if (notifications.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Todavía no hay notificaciones para mostrar")
            }
        } else {
            LazyColumn {
                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->
                    Column(
                        modifier = Modifier
                            .clickable { onNotificationClick(notification) }
                            .padding(vertical = 12.dp)
                    ) {
                        Text(
                            text = notification.title,
                            fontWeight = if (notification.isRead) {
                                FontWeight.Normal
                            } else {
                                FontWeight.Bold
                            },
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(text = notification.body)
                    }
                }
            }
        }
    }
}