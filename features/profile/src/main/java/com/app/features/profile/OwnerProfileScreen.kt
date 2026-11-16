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
import com.app.features.profile.component.PersonalInformationSection
import com.app.features.profile.component.ProfileActions
import com.app.features.profile.component.ProfileAppearanceSection
import com.app.features.profile.component.ProfileHeader

@Composable
fun OwnerProfileRoute(
    userId: String,
    onSignOut: () -> Unit,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    viewModel: OwnerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    OwnerProfileScreen(
        state = state,
        darkTheme = darkTheme,
        onDarkThemeChange = onDarkThemeChange,
        onNameChange = viewModel::onNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onSave = viewModel::saveProfile,
        onRetry = viewModel::retryLoadProfile,
        onPhotoSelected = viewModel::uploadProfilePhoto,
        onSignOut = onSignOut
    )
}

@Composable
private fun OwnerProfileScreen(
    state: OwnerProfileUiState,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onPhotoSelected: (Uri) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPhotoUri by remember {
        mutableStateOf<Uri?>(null)
    }

    LaunchedEffect(state.isUploadingPhoto, state.photoErrorMessage) {
        if (!state.isUploadingPhoto) {
            selectedPhotoUri = null
        }
    }

    when {
        state.isLoading -> {
            LoadingContent(modifier = modifier.fillMaxSize())
        }

        state.errorMessage != null && state.userId.isBlank() -> {
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
                    verticalArrangement = Arrangement.spacedBy(DimensUI.SpaceLarge)
                ) {
                    Text(
                        text = "Mi perfil",
                        style = MaterialTheme.typography.headlineLarge
                    )
                    DomainErrorMessage(
                        message = state.errorMessage,
                        onRetry = onRetry
                    )
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
                        text = "Mi perfil",
                        style = MaterialTheme.typography.headlineLarge
                    )

                    ProfileHeader(
                        name = state.name,
                        lastName = state.lastName,
                        roleLabel = "Dueño de mascotas",
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
                            color = MaterialTheme.colorScheme.error
                        )
                    }

                    HorizontalDivider()

                    ProfileAppearanceSection(
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange
                    )

                    HorizontalDivider()

                    PersonalInformationSection(
                        name = state.name,
                        lastName = state.lastName,
                        email = state.email,
                        phone = state.phone,
                        enabled = !state.isSaving,
                        onNameChange = onNameChange,
                        onLastNameChange = onLastNameChange,
                        onPhoneChange = onPhoneChange
                    )

                    ProfileActions(
                        isSaving = state.isSaving,
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
private fun OwnerProfileScreenPreview() {
    MascotasThemeUI {
        OwnerProfileScreen(
            state = OwnerProfileUiState(
                userId = "owner-1",
                name = "Sofía",
                lastName = "García",
                email = "sofia@example.com",
                phone = "11 2345 6789",
                isLoading = false
            ),
            darkTheme = false,
            onDarkThemeChange = {},
            onNameChange = {},
            onLastNameChange = {},
            onPhoneChange = {},
            onSave = {},
            onRetry = {},
            onPhotoSelected = {},
            onSignOut = {}
        )
    }
}

@Preview(
    name = "Perfil dueño en ventana amplia",
    showBackground = true,
    widthDp = 840,
    heightDp = 1200
)
@Composable
private fun OwnerProfileScreenWidePreview() {
    OwnerProfileScreenPreview()
}
