package com.app.features.walks.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.app.core.domain.model.Walk
import com.app.core.domain.model.WalkRequest
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.model.RequestStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun WalkCard(
    walk: Walk,
    isProcessing: Boolean = false,
    showStart: Boolean = false,
    showComplete: Boolean = false,
    showTrack: Boolean = false,
    showCancel: Boolean = false,
    onStart: () -> Unit = {},
    onComplete: () -> Unit = {},
    onTrack: () -> Unit = {},
    onCancel: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Paseo", style = MaterialTheme.typography.titleMedium)
                Text(statusLabel(walk.status), style = MaterialTheme.typography.labelLarge, color = statusColor(walk.status))
            }
            Text(formatScheduledAt(walk.scheduledAt), style = MaterialTheme.typography.bodyMedium)
            Text("Duración: ${walk.durationMinutes} minutos", style = MaterialTheme.typography.bodyMedium)
            Text("Mascotas: ${walk.petIds.size}", style = MaterialTheme.typography.bodyMedium)
            if (walk.observations.isNotBlank()) Text("Observaciones: ${walk.observations}", style = MaterialTheme.typography.bodySmall)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                if (showStart) TextButton(onClick = onStart, enabled = !isProcessing) { Text(if (isProcessing) "Procesando…" else "Iniciar paseo") }
                if (showComplete) TextButton(onClick = onComplete, enabled = !isProcessing) { Text(if (isProcessing) "Procesando…" else "Finalizar paseo") }
                if (showTrack) TextButton(onClick = onTrack) { Text("Seguimiento") }
                if (showCancel) TextButton(onClick = onCancel, enabled = !isProcessing) { Text("Cancelar") }
            }
        }
    }
}

@Composable
fun WalkRequestCard(
    request: WalkRequest,
    isProcessing: Boolean = false,
    onCancel: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerLow)
    ) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text("Solicitud de paseo", style = MaterialTheme.typography.titleMedium)
                Text(requestStatusLabel(request.status), style = MaterialTheme.typography.labelLarge, color = if (request.status == RequestStatus.REJECTED) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary)
            }
            Text(formatScheduledAt(request.scheduledAt), style = MaterialTheme.typography.bodyMedium)
            Text("Duración: ${request.durationMinutes} minutos", style = MaterialTheme.typography.bodyMedium)
            Text("Mascotas: ${request.petIds.size}", style = MaterialTheme.typography.bodyMedium)
            if (request.observations.isNotBlank()) Text("Observaciones: ${request.observations}", style = MaterialTheme.typography.bodySmall)
            if (request.status == RequestStatus.PENDING) {
                TextButton(onClick = onCancel, enabled = !isProcessing) { Text(if (isProcessing) "Procesando…" else "Cancelar solicitud") }
            }
        }
    }
}

private fun statusLabel(status: WalkStatus): String = when (status) {
    WalkStatus.SCHEDULED -> "Confirmado"
    WalkStatus.IN_PROGRESS -> "En curso"
    WalkStatus.COMPLETED -> "Finalizado"
    WalkStatus.CANCELLED -> "Cancelado"
}

private fun requestStatusLabel(status: RequestStatus): String = when (status) {
    RequestStatus.PENDING -> "Pendiente"
    RequestStatus.ACCEPTED -> "Aceptada"
    RequestStatus.REJECTED -> "Rechazada"
    RequestStatus.CANCELLED -> "Cancelada"
}

@Composable
private fun statusColor(status: WalkStatus) = when (status) {
    WalkStatus.SCHEDULED -> MaterialTheme.colorScheme.primary
    WalkStatus.IN_PROGRESS -> MaterialTheme.colorScheme.tertiary
    WalkStatus.COMPLETED -> MaterialTheme.colorScheme.secondary
    WalkStatus.CANCELLED -> MaterialTheme.colorScheme.error
}

private fun formatScheduledAt(value: String): String {
    if (value.isBlank()) return "Fecha sin especificar"
    return runCatching {
        val dateTime = runCatching { LocalDateTime.parse(value) }
            .getOrElse { LocalDateTime.ofInstant(Instant.parse(value), ZoneId.systemDefault()) }
        dateTime.format(DateTimeFormatter.ofPattern("dd/MM/yyyy 'a las' HH:mm", Locale("es", "AR")))
    }.getOrDefault(value)
}
