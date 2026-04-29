package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.myparkingareas

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreasUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
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

    fun onSearchQueryChange(query: String) {
        _state.value = _state.value.copy(
            searchQuery = query,
            filteredParkingAreas = _state.value.parkingAreas?.filter {
                it.name.contains(query.trim(), ignoreCase = true)
            }
        )
    }

    fun loadParkingAreas() {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getUserIdUseCase()
                .onSuccess { userId ->
                    getParkingAreasUseCase(userId)
                        .onSuccess { list ->
                            _state.value = _state.value.copy(
                                parkingAreas = list,
                                filteredParkingAreas = list,
                                isLoading = false
                            )
                        }
                        .onFailure {
                            _state.value = _state.value.copy(
                                error = ErrorMapper.map(it),
                                isLoading = false
                            )
                        }
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it),
                        isLoading = false
                    )
                }
        }
    }

}