package com.app.core.ui.components

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun ProfilePhotoPicker(
    onPhotoSelected: (Uri) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (onOpenPicker: () -> Unit) -> Unit
) {
    val context = LocalContext.current

    var showOptions by remember {
        mutableStateOf(false)
    }

    var cameraPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            onPhotoSelected(uri)
        }
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            cameraPhotoUri?.let(onPhotoSelected)
        }
    }

    content {
        showOptions = true
    }

    if (showOptions) {
        AlertDialog(
            onDismissRequest = {
                showOptions = false
            },
            title = {
                Text("Foto de perfil")
            },
            text = {
                Column(
                    verticalArrangement = Arrangement.spacedBy(
                        androidx.compose.ui.unit.Dp(8f)
                    )
                ) {
                    TextButton(
                        onClick = {
                            showOptions = false

                            val uri = createProfilePhotoUri(context)
                            cameraPhotoUri = uri
                            cameraLauncher.launch(uri)
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Tomar foto con cámara")
                    }

                    TextButton(
                        onClick = {
                            showOptions = false

                            galleryLauncher.launch(
                                PickVisualMediaRequest(
                                    ActivityResultContracts
                                        .PickVisualMedia
                                        .ImageOnly
                                )
                            )
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("Elegir de la galería")
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showOptions = false
                    }
                ) {
                    Text("Cancelar")
                }
            }
        )
    }
}

private fun createProfilePhotoUri(
    context: Context
): Uri {
    val directory = File(
        context.cacheDir,
        "profile_photos"
    )

    if (!directory.exists()) {
        directory.mkdirs()
    }

    val file = File.createTempFile(
        "profile_",
        ".jpg",
        directory
    )

    return FileProvider.getUriForFile(
        context,
        "${context.packageName}.fileprovider",
        file
    )
}