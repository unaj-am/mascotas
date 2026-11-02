
package com.app.features.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.NotificationsNone
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil3.compose.AsyncImage
import com.app.core.ui.components.AppAvatar
import com.app.core.ui.components.IconActionButton
import com.app.core.ui.theme.DimensUI

@Composable
fun HomeHeader(
    userName: String,
    onNotificationsClick: () -> Unit,
    notificationCount: Int = 0,
    profileImage: String? = null,
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
            modifier = Modifier
                .size(DimensUI.AvatarSizeLarge)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.primaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            AppAvatar(
                name = userName.ifBlank { "Usuario" },
                size = DimensUI.AvatarSizeLarge,
                backgroundColor =
                    MaterialTheme.colorScheme.primaryContainer,
                contentColor =
                    MaterialTheme.colorScheme.onPrimaryContainer
            )

            if (!profileImage.isNullOrBlank()) {
                AsyncImage(
                    model = profileImage,
                    contentDescription = "Foto de perfil",
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
                text = "HOLA DE NUEVO",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary
            )

            Text(
                text = userName.ifBlank { "Bienvenido" },
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onBackground
            )

            Text(
                text = "¿Listos para el próximo paseo?",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Box {
            IconActionButton(
                icon = Icons.Filled.NotificationsNone,
                contentDescription = "Notificaciones",
                onClick = onNotificationsClick
            )

            if (notificationCount > 0) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 4.dp, y = (-4).dp)
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDC2626)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (notificationCount > 99) {
                            "99+"
                        } else {
                            notificationCount.toString()
                        },
                        color = Color.White,
                        fontSize = if (notificationCount > 99) {
                            9.sp
                        } else {
                            11.sp
                        },
                        fontWeight = FontWeight.Bold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}
