
package com.app.features.auth.login

import com.app.core.domain.model.UserRole

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val userId: String? = null,
    val userRole: UserRole? = null,
    val errorMessage: String? = null
)