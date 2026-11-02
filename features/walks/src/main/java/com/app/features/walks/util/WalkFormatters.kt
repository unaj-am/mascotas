
package com.app.features.walks.util

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.app.core.domain.model.WalkStatus
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@Composable
fun walkStatusBackgroundColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED ->
            MaterialTheme.colorScheme.primaryContainer

        WalkStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.tertiaryContainer

        WalkStatus.COMPLETED ->
            MaterialTheme.colorScheme.secondaryContainer

        WalkStatus.CANCELLED ->
            MaterialTheme.colorScheme.errorContainer
    }

@Composable
fun walkStatusTextColor(status: WalkStatus) =
    when (status) {
        WalkStatus.SCHEDULED ->
            MaterialTheme.colorScheme.onPrimaryContainer

        WalkStatus.IN_PROGRESS ->
            MaterialTheme.colorScheme.onTertiaryContainer

        WalkStatus.COMPLETED ->
            MaterialTheme.colorScheme.onSecondaryContainer

        WalkStatus.CANCELLED ->
            MaterialTheme.colorScheme.onErrorContainer
    }

fun walkStatusShortLabel(status: WalkStatus): String =
    when (status) {
        WalkStatus.SCHEDULED -> "Confirmado"
        WalkStatus.IN_PROGRESS -> "En curso"
        WalkStatus.COMPLETED -> "Finalizado"
        WalkStatus.CANCELLED -> "Cancelado"
    }

fun walkStatusLabel(status: WalkStatus): String =
    when (status) {
        WalkStatus.SCHEDULED -> "Paseo confirmado"
        WalkStatus.IN_PROGRESS -> "Paseo en curso"
        WalkStatus.COMPLETED -> "Paseo finalizado"
        WalkStatus.CANCELLED -> "Paseo cancelado"
    }

fun formatWalkDate(
    value: String,
    emptyValue: String = "Sin especificar"
): String {
    if (value.isBlank()) return emptyValue

    return runCatching {
        val dateTime = runCatching {
            LocalDateTime.parse(value)
        }.getOrElse {
            LocalDateTime.ofInstant(
                Instant.parse(value),
                ZoneId.systemDefault()
            )
        }

        dateTime.format(
            DateTimeFormatter.ofPattern(
                "dd/MM/yyyy 'a las' HH:mm",
                Locale("es", "AR")
            )
        )
    }.getOrDefault(value)
}
