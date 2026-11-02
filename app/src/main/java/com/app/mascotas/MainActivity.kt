
package com.app.mascotas

import android.Manifest
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.app.core.domain.model.UserRole
import com.app.core.data.notifications.MascotasNotificationChannel
import com.app.core.ui.components.DomainErrorMessage
import com.app.core.ui.components.LoadingContent
import com.app.core.ui.components.SecondaryActionButton
import com.app.core.ui.theme.DimensUI
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.auth.login.LoginScreen
import com.app.features.auth.register.RegisterScreen
import com.app.features.auth.verification.EmailVerificationScreen
import com.app.mascotas.navigation.AppSessionState
import com.app.mascotas.navigation.OwnerNavigation
import com.app.mascotas.navigation.WalkerNavigation
import com.app.mascotas.session.AppSessionViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        MascotasNotificationChannel.create(this)

        setContent {

            val notificationPermissionLauncher =
                rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestPermission(),
                    onResult = { isGranted ->
                        // Más adelante podemos manejar si el usuario rechaza el permiso.
                    }
                )

            LaunchedEffect(Unit) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(
                        Manifest.permission.POST_NOTIFICATIONS
                    )
                }
            }

            val sessionViewModel: AppSessionViewModel =
                hiltViewModel()

            val sessionState by sessionViewModel.uiState
                .collectAsStateWithLifecycle()

            var showRegister by rememberSaveable {
                mutableStateOf(false)
            }

            // Preferencias de apariencia.
            val appearancePreferences = remember {
                getSharedPreferences(
                    APPEARANCE_PREFERENCES,
                    MODE_PRIVATE
                )
            }

            // Si no existe una preferencia guardada, se respeta
            // inicialmente la configuración del dispositivo.
            val systemDarkTheme = isSystemInDarkTheme()

            var darkTheme by remember {
                mutableStateOf(
                    appearancePreferences.getBoolean(
                        KEY_DARK_THEME,
                        systemDarkTheme
                    )
                )
            }

            val isWalkerTheme =
                (sessionState as? AppSessionState.Authenticated)
                    ?.role == UserRole.WALKER

            MascotasThemeUI(
                isWalker = isWalkerTheme,
                darkTheme = darkTheme
            ) {
                if (showRegister) {
                    BackHandler {
                        showRegister = false
                    }

                    RegisterScreen(
                        onRegisterSuccess = {
                            showRegister = false
                        },
                        onNavigateToLogin = {
                            showRegister = false
                        }
                    )
                } else {
                    when (val state = sessionState) {

                        AppSessionState.Loading -> {
                            LoadingContent(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .safeDrawingPadding(),
                                message = "Verificando sesión..."
                            )
                        }

                        AppSessionState.SignedOut -> {
                            LoginScreen(
                                onLoginSuccess = {
                                    // Firebase notifica el cambio de sesión.
                                    // AppSessionViewModel valida el perfil.
                                },
                                onNavigateToRegister = {
                                    showRegister = true
                                }
                            )
                        }

                        is AppSessionState.NeedsVerification -> {
                            EmailVerificationScreen(
                                isLoading = state.isLoading,
                                message = state.message,
                                onCheckVerification = {
                                    sessionViewModel.retrySessionValidation()
                                },
                                onResendEmail = {
                                    sessionViewModel.resendVerificationEmail()
                                },
                                onSignOut = {
                                    sessionViewModel.signOut()
                                }
                            )
                        }

                        is AppSessionState.Authenticated -> {
                            when (state.role) {
                                UserRole.OWNER -> {
                                    OwnerNavigation(
                                        ownerId = state.userId,
                                        onSignOut = {
                                            sessionViewModel.signOut()
                                        },
                                        darkTheme = darkTheme,
                                        onDarkThemeChange = { enabled ->
                                            darkTheme = enabled
                                            appearancePreferences.edit()
                                                .putBoolean(KEY_DARK_THEME, enabled)
                                                .apply()
                                        }
                                    )
                                }

                                UserRole.WALKER -> {
                                    WalkerNavigation(
                                        userId = state.userId,
                                        onSignOut = {
                                            sessionViewModel.signOut()
                                        },
                                        darkTheme = darkTheme,
                                        onDarkThemeChange = { enabled ->
                                            darkTheme = enabled
                                            appearancePreferences.edit()
                                                .putBoolean(KEY_DARK_THEME, enabled)
                                                .apply()
                                        }
                                    )
                                }
                            }
                        }

                        is AppSessionState.Error -> {
                            SessionErrorContent(
                                message = state.message,
                                onRetry = {
                                    sessionViewModel.retrySessionValidation()
                                },
                                onSignOut = {
                                    sessionViewModel.signOut()
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val APPEARANCE_PREFERENCES = "appearance_preferences"
        private const val KEY_DARK_THEME = "dark_theme"
    }
}

@Composable
private fun SessionErrorContent(
    message: String,
    onRetry: () -> Unit,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .safeDrawingPadding()
            .padding(
                horizontal = DimensUI.ScreenHorizontalPadding,
                vertical = DimensUI.ScreenVerticalPadding
            ),
        verticalArrangement = Arrangement.spacedBy(
            DimensUI.SpaceLarge,
            Alignment.CenterVertically
        ),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        DomainErrorMessage(
            message = message,
            onRetry = onRetry
        )

        SecondaryActionButton(
            text = "Cerrar sesión",
            onClick = onSignOut
        )
    }
}
