
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.theme.DimensUI
import com.app.features.profile.component.PersonalInformationSection
import com.app.features.profile.component.ProfileActions
import com.app.features.profile.component.ProfileHeader

@Composable
fun OwnerProfileRoute(
    userId: String,
    onSignOut: () -> Unit,
    viewModel: OwnerProfileViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(userId) {
        viewModel.loadProfile(userId)
    }

    OwnerProfileScreen(
        state = state,
        onNameChange = viewModel::onNameChange,
        onLastNameChange = viewModel::onLastNameChange,
        onPhoneChange = viewModel::onPhoneChange,
        onSave = viewModel::saveProfile,
        onRetry = viewModel::retryLoadProfile,
        onSignOut = onSignOut
    )
}

@Composable
private fun OwnerProfileScreen(
    state: OwnerProfileUiState,
    onNameChange: (String) -> Unit,
    onLastNameChange: (String) -> Unit,
    onPhoneChange: (String) -> Unit,
    onSave: () -> Unit,
    onRetry: () -> Unit,
    onSignOut: () -> Unit
) {
    when {
        state.isLoading -> {
            LoadingContent()
        }

        state.errorMessage != null && state.userId.isBlank() -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .safeDrawingPadding()
                    .padding(DimensUI.ScreenHorizontalPadding),
                verticalArrangement = Arrangement.spacedBy(
                    DimensUI.SpaceLarge
                )
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
                    text = "Mi perfil",
                    style = MaterialTheme.typography.headlineLarge
                )

                ProfileHeader(
                    name = state.name,
                    lastName = state.lastName,
                    roleLabel = "Dueño de mascotas"
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
