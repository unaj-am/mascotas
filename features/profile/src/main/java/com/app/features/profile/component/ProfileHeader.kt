package com.app.features.profile.component

import android.graphics.BitmapFactory
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage

@Composable
fun ProfileHeader(
    name: String,
    lastName: String,
    roleLabel: String,
    photoUri: Uri? = null,
    profileImageUrl: String? = null
)  {
    val context = LocalContext.current

    val bitmap = remember(photoUri) {
        photoUri?.let { uri ->
            runCatching {
                context.contentResolver.openInputStream(uri)?.use { stream ->
                    BitmapFactory.decodeStream(stream)
                }
            }.getOrNull()
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(CircleShape)
                .background(
                    MaterialTheme.colorScheme.secondaryContainer
                ),
            contentAlignment = Alignment.Center
        ) {
            when {
                bitmap != null -> {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                !profileImageUrl.isNullOrBlank() -> {
                    AsyncImage(
                        model = profileImageUrl,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier
                            .size(88.dp)
                            .clip(CircleShape),
                        contentScale = ContentScale.Crop
                    )
                }

                else -> {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Foto de perfil",
                        modifier = Modifier.size(44.dp),
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        Text(
            text = listOf(name, lastName)
                .filter { it.isNotBlank() }
                .joinToString(" ")
                .ifBlank { "Mi perfil" },
            style = MaterialTheme.typography.headlineSmall
        )

        Text(
            text = roleLabel,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}