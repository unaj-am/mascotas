
package com.app.features.profile.component

import android.net.Uri
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import coil3.compose.rememberAsyncImagePainter
import com.app.core.ui.components.AppAvatar
import com.app.core.ui.components.AppProfileHeader
import com.app.core.ui.theme.DimensUI

@Composable
fun ProfileHeader(
    name: String,
    lastName: String,
    roleLabel: String,
    photoUri: Uri? = null,
    profileImageUrl: String? = null
) {
    val fullName = listOf(name, lastName)
        .filter { it.isNotBlank() }
        .joinToString(" ")
        .ifBlank { "Mi perfil" }

    val imageModel: Any? = photoUri
        ?: profileImageUrl?.takeIf { it.isNotBlank() }

    val imagePainter = imageModel?.let {
        rememberAsyncImagePainter(model = it)
    }

    AppProfileHeader(
        name = fullName,
        subtitle = roleLabel,
        avatar = {
            AppAvatar(
                name = fullName,
                modifier = Modifier,
                image = imagePainter,
                size = DimensUI.AvatarSizeXLarge,
                description = "Foto de perfil de $fullName"
            )
        }
    )
}
