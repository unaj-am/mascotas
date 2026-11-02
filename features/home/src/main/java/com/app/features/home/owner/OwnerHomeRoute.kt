
package com.app.features.home.owner

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent

@Composable
fun OwnerHomeRoute(
    ownerId: String,
    onNotificationsClick: () -> Unit,
    onFindWalkerClick: () -> Unit,
    isVisible: Boolean = true,
    notificationCount: Int = 0,
    viewModel: OwnerHomeViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(ownerId, isVisible) {
        if (isVisible) {
            viewModel.loadHome(
                ownerId = ownerId,
                forceRefresh = true
            )
        }
    }

    when {
        state.isLoading -> {
            LoadingContent()
        }

        state.errorMessage != null -> {
            DomainErrorMessage(
                message = state.errorMessage.orEmpty(),
                onRetry = viewModel::retryLoadHome
            )
        }

        else -> {
            OwnerHomeScreen(
                userName = state.userName,
                profileImage = state.profileImage,
                petsCount = state.petsCount,
                upcomingWalksCount = state.upcomingWalksCount,
                nextPetName = state.nextPetName,
                nextPetBreed = state.nextPetBreed,
                nextWalkDate = state.nextWalkDate,
                notificationCount = notificationCount,
                onNotificationsClick = onNotificationsClick,
                onFindWalkerClick = onFindWalkerClick
            )
        }
    }
}
