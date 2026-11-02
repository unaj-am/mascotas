
package com.app.features.walkers.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetWalkersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerViewModel @Inject constructor(
    private val getWalkersUseCase: GetWalkersUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<WalkerUiState>(WalkerUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        loadWalkers()
    }

    fun loadWalkers() {
        viewModelScope.launch {
            if (_uiState.value !is WalkerUiState.Success) {
                _uiState.value = WalkerUiState.Loading
            }

            try {
                // Firestore devuelve únicamente paseadores disponibles.
                val walkers = getWalkersUseCase()

                _uiState.value = WalkerUiState.Success(
                    walkers = walkers
                )

            } catch (exception: CancellationException) {
                throw exception

            } catch (exception: Exception) {
                _uiState.value = WalkerUiState.Error(
                    message = exception.message
                        ?: "No se pudieron cargar los paseadores."
                )
            }
        }
    }
}
