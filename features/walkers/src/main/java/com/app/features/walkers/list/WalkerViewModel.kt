
package com.app.features.walkers.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.core.domain.usecase.GetAvailableWalkersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class WalkerViewModel @Inject constructor(
    private val getAvailableWalkersUseCase: GetAvailableWalkersUseCase
) : ViewModel() {

    private val _uiState =
        MutableStateFlow<WalkerUiState>(WalkerUiState.Loading)

    val uiState = _uiState.asStateFlow()

    init {
        loadWalkers()
    }

    fun loadWalkers() {
        viewModelScope.launch {
            _uiState.value = WalkerUiState.Loading

            try {
                val walkers = getAvailableWalkersUseCase()

                _uiState.value = WalkerUiState.Success(
                    walkers = walkers
                )
            } catch (exception: CancellationException) {
                throw exception
            } catch (exception: Exception) {
                _uiState.value = WalkerUiState.Error(
                    message = exception.message
                        ?: "No se pudieron cargar los paseadores disponibles."
                )
            }
        }
    }
}
