package es.ubu.bikeparkingapp.presentation.feature.parking.myparkingareas

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de lista de parkings.
 * @property getParkingAreasUseCase Caso de uso para obtener la lista de parkings.
 * @property getUserIdUseCase Caso de uso para obtener el ID del usuario.
 */
class MyParkingAreasViewModel(
    val getParkingAreasUseCase: GetParkingAreasUseCase,
    val getUserIdUseCase: GetUserIdUseCase
): ViewModel() {
    private val _state = mutableStateOf(MyParkingAreasState())
    val state: State<MyParkingAreasState> = _state

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun loadParkingAreas() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getUserIdUseCase()
                .onSuccess { userId ->
                    getParkingAreasUseCase(userId)
                        .onSuccess { list ->
                            _state.value = _state.value.copy(parkingAreas = list, isLoading = false)
                        }
                        .onFailure { error ->
                            _state.value = _state.value.copy(error = error as? Exception, isLoading = false)
                        }
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(error = error as? Exception, isLoading = false)
                }
        }
    }

}