
package com.app.mascotas.navigation

import com.app.core.domain.model.UserRole

sealed interface AppSessionState {

    data object Loading : AppSessionState

    data object SignedOut : AppSessionState

    data class NeedsVerification(
        val message: String? = null,
        val isLoading: Boolean = false
    ) : AppSessionState

    data class Authenticated(
        val userId: String,
        val role: UserRole
    ) : AppSessionState

    data class Error(
        val message: String
    ) : AppSessionState
}
