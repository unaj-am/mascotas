package com.app.features.notifications.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.AppNotification
import com.app.core.ui.components.AppCard
import com.app.core.ui.components.AppTopBar
import com.app.core.ui.components.EmptyContent
import com.app.core.ui.components.StatusBadge
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun NotificationsScreen(
    notifications: List<AppNotification> = emptyList(),
    modifier: Modifier = Modifier,
    onNotificationClick: (AppNotification) -> Unit = {},
    onBackClick: () -> Unit = {},
    onClearAllClick: () -> Unit = {}
) {
    var showClearConfirmation by remember {
        mutableStateOf(false)
    }

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 880.dp)
                .fillMaxSize()
                .padding(horizontal = DimensUI.ScreenHorizontalPadding),
            verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceSmall)
        ) {
            AppTopBar(
                title = "Notificaciones",
                subtitle = if (notifications.isEmpty()) {
                    "Estás al día"
                } else {
                    "${notifications.size} notificaciones"
                },
                navigationIcon = {
                    TextButton(onClick = onBackClick) {
                        Text("Volver")
                    }
                },
                actions = {
                    if (notifications.any { !it.isRead }) {
                        TextButton(onClick = { showClearConfirmation = true }) {
                            Text("Marcar todas")
                        }
                    }
                }
            )

            if (notifications.isEmpty()) {
                EmptyContent(
                    title = "No tenés notificaciones",
                    message = "Cuando haya novedades sobre tus paseos, las vas a encontrar acá.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(vertical = DimensUI.SpaceSmall),
                    verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceSmall)
                ) {
                    items(
                        items = notifications,
                        key = { notification -> notification.id }
                    ) { notification ->
                        NotificationCard(
                            notification = notification,
                            onClick = { onNotificationClick(notification) }
                        )
                    }
                }
            }
        }
    }

    if (showClearConfirmation) {
        AlertDialog(
            onDismissRequest = { showClearConfirmation = false },
            title = { Text("Marcar notificaciones como leídas") },
            text = { Text("¿Querés marcar todas las notificaciones como leídas?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showClearConfirmation = false
                        onClearAllClick()
                    }
                ) {
                    Text("Marcar como leídas")
                }
            },
            dismissButton = {
                TextButton(onClick = { showClearConfirmation = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

@Composable
private fun NotificationCard(
    notification: AppNotification,
    onClick: () -> Unit
) {
    AppCard(
        modifier = Modifier.fillMaxWidth(),
        onClick = onClick,
        contentPadding = DimensUI.CardPadding
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceSmall)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = notification.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = if (notification.isRead) {
                        FontWeight.Medium
                    } else {
                        FontWeight.Bold
                    },
                    modifier = Modifier.weight(1f)
                )

                if (!notification.isRead) {
                    StatusBadge(text = "Nueva")
                }
            }

            Text(
                text = notification.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 3,
                overflow = TextOverflow.Ellipsis
            )

            if (notification.createdAt > 0L) {
                Text(
                    text = formatNotificationDate(notification.createdAt),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

private fun formatNotificationDate(createdAt: Long): String =
    DateTimeFormatter.ofPattern("d MMM, HH:mm", Locale.forLanguageTag("es-AR"))
        .withZone(ZoneId.systemDefault())
        .format(Instant.ofEpochMilli(createdAt))

@Preview(showBackground = true)
@Composable
private fun NotificationsScreenPreview() {
    val notifications = listOf(
        AppNotification(
            id = "notification-1",
            title = "Nueva solicitud de paseo",
            body = "Mateo aceptó la solicitud para pasear a Luna mañana.",
            isRead = false,
            createdAt = 1_767_000_000_000L
        ),
        AppNotification(
            id = "notification-2",
            title = "Paseo completado",
            body = "El paseo de Firulais finalizó correctamente.",
            isRead = true,
            createdAt = 1_766_900_000_000L
        )
    )

    MascotasThemeUI {
        NotificationsScreen(notifications = notifications)
    }
}

@Preview(
    name = "Notificaciones en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun NotificationsScreenWidePreview() {
    NotificationsScreenPreview()
}

@Preview(showBackground = true)
@Composable
private fun NotificationsEmptyPreview() {
    MascotasThemeUI {
        NotificationsScreen()
    }
}
