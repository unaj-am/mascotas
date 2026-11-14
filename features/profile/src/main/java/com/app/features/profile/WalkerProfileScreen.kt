package com.app.features.profile

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.ProfilePhotoPicker
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.profile.component.ProfileAppearanceSection
import com.app.features.profile.component.ProfileHeader
import com.app.features.profile.component.WalkerPersonalInfoSection
import com.app.features.profile.component.WalkerProfessionalInfoSection
import com.app.features.profile.component.WalkerProfileActions

@Composable
fun WalkerProfileScreen(
    userId: String,
    onSignOut: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    viewModel: WalkerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    WalkerProfileContent(
        state = state,
        darkTheme = darkTheme,
        onDarkThemeChange = onDarkThemeChange,
        onRetry = viewModel::retryLoadProfile,
        onPhotoSelected = viewModel::uploadProfilePhoto,
        onNameChange = viewModel::onNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onDescriptionChange = viewModel::onDescriptionChange,
        onExperienceYearsChange = viewModel::onExperienceYearsChange,
        onServiceAreaChange = viewModel::onServiceAreaChange,
        onPricePerWalkChange = viewModel::onPricePerWalkChange,
        onMaxPetsChange = viewModel::onMaxPetsChange,
        onSave = viewModel::saveProfile,
        onSignOut = onSignOut
    )
}

@Composable
private fun WalkerProfileContent(
    state: WalkerProfileUiState,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onRetry: () -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onExperienceYearsChange: (String) -> Unit,
    onServiceAreaChange: (String) -> Unit,
    onPricePerWalkChange: (String) -> Unit,
    onMaxPetsChange: (String) -> Unit,
    onSave: () -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
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
            LoadingContent(modifier = modifier.fillMaxSize())
        }

        state.errorMessage != null &&
            state.name.isBlank() &&
            state.email.isBlank() -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .padding(DimensUI.ScreenHorizontalPadding),
                    verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceLarge),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    DomainErrorMessage(
                        message = state.errorMessage,
                        onRetry = onRetry
                    )
                    TextButton(onClick = onSignOut) {
                        Text("Cerrar sesión")
                    }
                }
            }
        }

        else -> {
            Box(
                modifier = modifier
                    .fillMaxSize()
                    .safeDrawingPadding(),
                contentAlignment = Alignment.TopCenter
            ) {
                Column(
                    modifier = Modifier
                        .widthIn(max = 720.dp)
                        .fillMaxWidth()
                        .verticalScroll(rememberScrollState())
                        .padding(
                            horizontal = DimensUI.ScreenHorizontalPadding,
                            vertical = DimensUI.ScreenVerticalPadding
                        ),
                    verticalArrangement = Arrangement.spacedBy(DimensUI.SectionSpacing)
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
                            onPhotoSelected(uri)
                        }
                    ) { openPicker ->
                        TextButton(
                            onClick = openPicker,
                            enabled = !state.isUploadingPhoto && !state.isSaving
                        ) {
                            Text(
                                if (state.isUploadingPhoto) {
                                    "Subiendo fotografía..."
                                } else {
                                    "Cambiar foto de perfil"
                                }
                            )
                        }
                    }

                    state.photoErrorMessage?.let { message ->
                        Text(
                            text = message,
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }

                    HorizontalDivider()

                    ProfileAppearanceSection(
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange
                    )

                    HorizontalDivider()

                    WalkerPersonalInfoSection(
                        name = state.name,
                        lastName = state.lastName,
                        email = state.email,
                        phone = state.phone,
                        isSaving = state.isSaving,
                        onNameChange = onNameChange,
                        onLastNameChange = onLastNameChange,
                        onPhoneChange = onPhoneChange
                    )

                    HorizontalDivider()

                    WalkerProfessionalInfoSection(
                        description = state.description,
                        experienceYears = state.experienceYears,
                        serviceArea = state.serviceArea,
                        pricePerWalk = state.pricePerWalk,
                        maxPets = state.maxPets,
                        isSaving = state.isSaving,
                        onDescriptionChange = onDescriptionChange,
                        onExperienceYearsChange = onExperienceYearsChange,
                        onServiceAreaChange = onServiceAreaChange,
                        onPricePerWalkChange = onPricePerWalkChange,
                        onMaxPetsChange = onMaxPetsChange
                    )

                    state.rating?.let { rating ->
                        Text(
                            text = "Calificación: ${"%.1f".format(rating)} / 5",
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }

                    WalkerProfileActions(
                        isSaving = state.isSaving,
                        isProfileCreated = state.isProfileCreated,
                        errorMessage = state.errorMessage,
                        successMessage = state.successMessage,
                        onSave = onSave,
                        onSignOut = onSignOut
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalkerProfileContentPreview() {
    MascotasThemeUI {
        WalkerProfileContent(
            state = WalkerProfileUiState(
                name = "Mateo",
                lastName = "Pérez",
                email = "mateo@example.com",
                phone = "11 9876 5432",
                description = "Paseos y cuidado responsable de mascotas.",
                experienceYears = "5",
                serviceArea = "Quilmes",
                pricePerWalk = "8000",
                maxPets = "3",
                rating = 4.8,
                isLoading = false,
                isProfileCreated = true
            ),
            darkTheme = false,
            onDarkThemeChange = {},
            onRetry = {},
            onPhotoSelected = {},
            onNameChange = {},
            onLastNameChange = {},
            onPhoneChange = {},
            onDescriptionChange = {},
            onExperienceYearsChange = {},
            onServiceAreaChange = {},
            onPricePerWalkChange = {},
            onMaxPetsChange = {},
            onSave = {},
            onSignOut = {}
        )
    }
}

@Preview(
    name = "Perfil paseador en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun WalkerProfileContentWidePreview() {
    WalkerProfileContentPreview()
}
