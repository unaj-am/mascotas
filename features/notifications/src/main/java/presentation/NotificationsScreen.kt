
package com.app.features.notifications.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.AppNotification

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification> = emptyList(),
    onNotificationClick: (AppNotification) -> Unit = {},
    onBackClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {}
) {
    var showClearConfirmation by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {

            TextButton(
                onClick = onBackClick
            ) {
                Text(
                    text = "‹ Volver"
                )
            }

            Text(
                text = "Notificaciones",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.weight(1f)
            )

            if (notifications.isNotEmpty()) {
                TextButton(
                    onClick = {
                        showClearConfirmation = true
                    }
                ) {
                    Text("Limpiar todas")
                }
            }
        }

        if (notifications.isEmpty()) {

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "No tenés notificaciones pendientes"
                )
            }

        } else {

            LazyColumn(
                modifier = Modifier.fillMaxSize()
            ) {

                items(
                    items = notifications,
                    key = { it.id }
                ) { notification ->

                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onNotificationClick(notification)
                            }
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

                        Text(
                            text = notification.body,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmation) {

        AlertDialog(
            onDismissRequest = {
                showClearConfirmation = false
            },
            title = {
                Text("Limpiar notificaciones")
            },
            text = {
                Text(
                    "¿Querés ocultar todas las notificaciones? " +
                            "No volverán a aparecer en la lista."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                        onClearAllClick()
                    }
                ) {
                    Text("Limpiar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}
