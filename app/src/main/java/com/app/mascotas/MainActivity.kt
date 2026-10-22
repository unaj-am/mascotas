
package com.app.mascotas

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModelProvider
import com.app.core.data.repository.PetRepositoryImpl
import com.app.core.domain.model.UserRole
import com.app.core.domain.usecase.DeletePetUseCase
import com.app.core.domain.usecase.GetPetByIdUseCase
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.SavePetUseCase
import com.app.core.domain.usecase.UpdatePetUseCase
import com.app.core.ui.theme.MascotasThemeUI
import com.app.features.auth.login.LoginScreen
import com.app.features.auth.register.RegisterScreen
import com.app.features.pets.detail.PetDetailViewModel
import com.app.features.pets.list.PetViewModel
import com.app.features.pets.list.PetViewModelFactory
import com.app.mascotas.navigation.OwnerNavigation
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val petRepository by lazy {
        PetRepositoryImpl(
            firestore = FirebaseFirestore.getInstance(),
            firebaseAuth = FirebaseAuth.getInstance()
        )
    }

    private val getPetsByOwnerUseCase by lazy {
        GetPetsByOwnerUseCase(petRepository)
    }

    private val getPetByIdUseCase by lazy {
        GetPetByIdUseCase(petRepository)
    }

    private val savePetUseCase by lazy {
        SavePetUseCase(petRepository)
    }

    private val updatePetUseCase by lazy {
        UpdatePetUseCase(petRepository)
    }

    private val deletePetUseCase by lazy {
        DeletePetUseCase(petRepository)
    }

    private val petViewModelFactory by lazy {
        PetViewModelFactory(
            getPetsByOwnerUseCase,
            savePetUseCase,
            updatePetUseCase,
            deletePetUseCase
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val petViewModel = ViewModelProvider(
            this,
            petViewModelFactory
        )[PetViewModel::class.java]

        val petDetailViewModel = PetDetailViewModel(
            getPetByIdUseCase
        )

        setContent {
            MascotasThemeUI {

                var currentUserId by rememberSaveable {
                    mutableStateOf<String?>(null)
                }

                var currentUserRole by rememberSaveable {
                    mutableStateOf<UserRole?>(null)
                }

                var showRegister by rememberSaveable {
                    mutableStateOf(false)
                }

                when {
                    currentUserId != null &&
                            currentUserRole == UserRole.OWNER -> {

                        OwnerNavigation(
                            ownerId = currentUserId!!,
                            petViewModel = petViewModel,
                            petDetailViewModel = petDetailViewModel
                        )
                    }

                    currentUserId != null &&
                            currentUserRole == UserRole.WALKER -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .safeDrawingPadding()
                                .padding(24.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "Panel del paseador pendiente de integración"
                            )
                        }
                    }

                    showRegister -> {
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
                    }

                    else -> {
                        LoginScreen(
                            onLoginSuccess = { userId, role ->
                                currentUserId = userId
                                currentUserRole = role
                                showRegister = false
                            },
                            onNavigateToRegister = {
                                showRegister = true
                            }
                        )
                    }
                }
            }
        }
    }
}
