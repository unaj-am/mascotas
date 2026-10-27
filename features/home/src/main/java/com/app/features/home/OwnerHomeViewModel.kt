package com.app.features.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.model.UserRole
import com.app.core.domain.model.WalkStatus
import com.app.core.domain.usecase.GetPetsByOwnerUseCase
import com.app.core.domain.usecase.GetUserByIdUseCase
import com.app.core.domain.usecase.GetWalksByOwnerUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import java.util.Locale
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel
class OwnerHomeViewModel @Inject constructor(
    private val getUserByIdUseCase: GetUserByIdUseCase,
    private val getPetsByOwnerUseCase: GetPetsByOwnerUseCase,
    private val getWalksByOwnerUseCase: GetWalksByOwnerUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OwnerHomeUiState())
    val uiState: StateFlow<OwnerHomeUiState> =
        _uiState.asStateFlow()

    private var currentOwnerId: String? = null

    fun loadHome(
        ownerId: String,
        forceRefresh: Boolean = false
    ) {
        if (ownerId.isBlank()) {
            _uiState.update {
                it.copy(
                    isLoading = false,
                    errorMessage =
                        "No se pudo identificar al dueño."
                )
            }
            return
        }

        if (
            !forceRefresh &&
            currentOwnerId == ownerId &&
            !_uiState.value.isLoading &&
            _uiState.value.errorMessage == null
        ) {
            return
        }

        currentOwnerId = ownerId

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = null
                )
            }

            try {
                val user = getUserByIdUseCase(ownerId)
                    ?: throw IllegalStateException(
                        "No se encontró el perfil del usuario."
                    )

                require(user.role == UserRole.OWNER) {
                    "La cuenta no corresponde a un dueño."
                }

                val pets = getPetsByOwnerUseCase(ownerId)
                val walks = getWalksByOwnerUseCase(ownerId)

                val scheduledWalks = walks
                    .filter {
                        it.status == WalkStatus.SCHEDULED
                    }
                    .sortedBy {
                        it.scheduledAt
                    }

                val nextWalk = scheduledWalks.firstOrNull()

                val nextPet = nextWalk?.petIds
                    ?.firstOrNull()
                    ?.let { petId ->
                        pets.firstOrNull { it.id == petId }
                    }

                _uiState.update {
                    it.copy(
                        userName = user.name,
                        petsCount = pets.size,
                        upcomingWalksCount =
                            scheduledWalks.size,
                        nextPetName = nextPet?.name,
                        nextPetBreed = nextPet?.breed,
                        nextWalkDate = nextWalk?.scheduledAt
                            ?.let(::formatScheduledDate),
                        isLoading = false,
                        errorMessage = null
                    )
                }
            } catch (error: CancellationException) {
                throw error
            } catch (error: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message
                            ?: "No se pudo cargar la información de inicio."
                    )
                }
            }
        }
    }

    fun retryLoadHome() {
        val ownerId = currentOwnerId ?: return

        loadHome(
            ownerId = ownerId,
            forceRefresh = true
        )
    }

    private fun formatScheduledDate(value: String): String {
        if (value.isBlank()) return value

        return runCatching {
            val dateTime = runCatching {
                LocalDateTime.parse(value)
            }.getOrElse {
                LocalDateTime.ofInstant(
                    Instant.parse(value),
                    ZoneId.systemDefault()
                )
            }

            dateTime.format(
                DateTimeFormatter
                    .ofLocalizedDateTime(FormatStyle.MEDIUM)
                    .withLocale(Locale("es", "AR"))
            )
        }.getOrDefault(value)
    }
}