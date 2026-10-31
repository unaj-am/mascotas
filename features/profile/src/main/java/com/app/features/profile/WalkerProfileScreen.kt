
package com.app.features.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.theme.DimensUI

import com.app.features.profile.component.WalkerPersonalInfoSection
import com.app.features.profile.component.WalkerProfessionalInfoSection
import com.app.features.profile.component.WalkerProfileActions

import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.app.core.ui.components.ProfilePhotoPicker
import com.app.features.profile.component.ProfileHeader

@Composable
fun WalkerProfileScreen(
    userId: String,
    onSignOut: () -> Unit,
    viewModel: WalkerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    var selectedPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }
    var wasUploadingPhoto by remember {
        mutableStateOf(false)
    }

    LaunchedEffect(state.isUploadingPhoto) {
        if (state.isUploadingPhoto) {
            wasUploadingPhoto = true
        } else if (wasUploadingPhoto) {
            selectedPhotoUri = null
            wasUploadingPhoto = false
        }
    }

    when {
        state.isLoading -> {
            LoadingContent()
        }

        state.errorMessage != null &&
                state.name.isBlank() &&
                state.email.isBlank() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(DimensUI.ScreenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                DomainErrorMessage(
                    message = state.errorMessage.orEmpty(),
                    onRetry = viewModel::retryLoadProfile
                )

                TextButton(onClick = onSignOut) {
                    Text("Cerrar sesión")
                }
            }
        }

        else -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .verticalScroll(rememberScrollState())
                    .padding(
                        horizontal = DimensUI.ScreenHorizontalPadding,
                        vertical = DimensUI.ScreenVerticalPadding
                    ),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SectionSpacing
                )
            ) {
                Text(
                    text = "Mi perfil de paseador",
                    style = MaterialTheme.typography.headlineLarge
                )

                ProfileHeader(
                    name = state.name,
                    lastName = state.lastName,
                    roleLabel = "Paseador",
                    photoUri = selectedPhotoUri,
                    profileImageUrl = state.profileImage
                )

                ProfilePhotoPicker(
                    onPhotoSelected = { uri ->
                        selectedPhotoUri = uri
                        viewModel.uploadProfilePhoto(uri)
                    }
                ) { openPicker ->
                    TextButton(
                        onClick = openPicker
                    ) {
                        Text("Cambiar foto de perfil")
                    }
                }

                state.photoErrorMessage?.let { message ->
                    Text(
                        text = message,
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                WalkerPersonalInfoSection(
                    name = state.name,
                    lastName = state.lastName,
                    email = state.email,
                    phone = state.phone,
                    isSaving = state.isSaving,
                    onNameChange = viewModel::onNameChange,
                    onLastNameChange = viewModel::onLastNameChange,
                    onPhoneChange = viewModel::onPhoneChange
                )

                HorizontalDivider()

                WalkerProfessionalInfoSection(
                    description = state.description,
                    experienceYears = state.experienceYears,
                    serviceArea = state.serviceArea,
                    pricePerWalk = state.pricePerWalk,
                    maxPets = state.maxPets,
                    isSaving = state.isSaving,
                    onDescriptionChange = viewModel::onDescriptionChange,
                    onExperienceYearsChange =
                        viewModel::onExperienceYearsChange,
                    onServiceAreaChange = viewModel::onServiceAreaChange,
                    onPricePerWalkChange =
                        viewModel::onPricePerWalkChange,
                    onMaxPetsChange = viewModel::onMaxPetsChange
                )

                state.rating?.let { rating ->
                    Text(
                        text = "Calificación: ${
                            "%.1f".format(rating)
                        } / 5",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }

                WalkerProfileActions(
                    isSaving = state.isSaving,
                    isProfileCreated = state.isProfileCreated,
                    errorMessage = state.errorMessage,
                    successMessage = state.successMessage,
                    onSave = viewModel::saveProfile,
                    onSignOut = onSignOut
                )
            }
        }
    }
}
