
package com.app.features.walks.walker.component

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.app.core.location.service.WalkTrackingService
import com.app.features.walks.walker.WalkerWalksViewModel

data class WalkerWalkActionCallbacks(
    val onStart: (String) -> Unit,
    val onComplete: (String) -> Unit
)

@Composable
fun rememberWalkerWalkActionCallbacks(
    viewModel: WalkerWalksViewModel
): WalkerWalkActionCallbacks {
    val context = LocalContext.current

    var pendingWalkId by remember {
        mutableStateOf<String?>(null)
    }

    fun startTracking(walkId: String) {
        runCatching {
            WalkTrackingService.start(context, walkId)
        }.onFailure {
            Toast.makeText(
                context,
                "El paseo comenzó, pero no se pudo iniciar el seguimiento GPS.",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    val locationPermissionLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            val granted =
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                        permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

            val walkId = pendingWalkId
            pendingWalkId = null

            if (granted && walkId != null) {
                viewModel.start(walkId) {
                    startTracking(walkId)
                }
            } else {
                Toast.makeText(
                    context,
                    "Se necesita permiso de ubicación para iniciar el paseo.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

    val onStart: (String) -> Unit = { walkId ->
        val hasFineLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        val hasCoarseLocation =
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED

        if (hasFineLocation || hasCoarseLocation) {
            viewModel.start(walkId) {
                startTracking(walkId)
            }
        } else {
            pendingWalkId = walkId

            locationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val onComplete: (String) -> Unit = { walkId ->
        viewModel.complete(walkId) {
            runCatching {
                WalkTrackingService.stop(context)
            }.onFailure {
                Toast.makeText(
                    context,
                    "El paseo finalizó, pero no se pudo detener el seguimiento GPS.",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    return WalkerWalkActionCallbacks(
        onStart = onStart,
        onComplete = onComplete
    )
}
