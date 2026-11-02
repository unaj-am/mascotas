
package com.app.features.walks.walker

import android.Manifest
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.location.service.WalkTrackingService

@Composable
fun WalkerActiveWalkRoute(
    walkerId: String,
    onBack: () -> Unit,
    viewModel: WalkerWalksViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()

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

    fun handleStart(walkId: String) {
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

    fun handleComplete(walkId: String) {
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

    LaunchedEffect(walkerId) {
        viewModel.load(
            walkerId = walkerId,
            forceRefresh = true
        )
    }

    WalkerActiveWalkScreen(
        state = state,
        onRefresh = viewModel::refresh,
        onStart = ::handleStart,
        onComplete = ::handleComplete,
        onBack = onBack
    )
}
