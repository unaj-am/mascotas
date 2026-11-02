
package com.app.features.home.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import coil3.compose.AsyncImage
import com.app.core.ui.components.AppAvatar
import com.app.core.ui.components.IconActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun WalkerHomeHeader(
    userName: String,
    profileImage: String? = null,
    onNotificationsClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceMedium
        )
    ) {

        Box(
            modifier = Modifier.size(DimensUI.AvatarSizeLarge),
            contentAlignment = Alignment.Center
        ) {
            AppAvatar(
                name = userName.ifBlank { "Paseador" },
                size = DimensUI.AvatarSizeLarge,
                backgroundColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                description = "Avatar del paseador"
            )

            if (!profileImage.isNullOrBlank()) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = "Foto de perfil del paseador",
                    modifier = Modifier
                        .size(DimensUI.AvatarSizeLarge)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(
                DimensUI.SpaceXSmall
            )
        ) {
            Text(
                text = "PANEL DE PASEADOR",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = "Hola, ${userName.ifBlank { "paseador" }}",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "Este es el resumen de tu actividad.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        IconActionButton(
            icon = Icons.Outlined.NotificationsNone,
            contentDescription = "Notificaciones",
            onClick = {
                onNotificationsClick?.invoke()
            },
            enabled = onNotificationsClick != null
        )
    }
}
