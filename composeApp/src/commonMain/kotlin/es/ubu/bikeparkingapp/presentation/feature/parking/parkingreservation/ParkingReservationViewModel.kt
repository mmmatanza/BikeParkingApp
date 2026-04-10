package es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.exception.NoNetworkException
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import kotlinx.coroutines.launch

/**
 * Representa el viewModel para la pantalla de reserva de plaza.
 * @property getParkingAreaByIdUseCase Caso de uso para obtener un parking por su id.
 */
class ParkingReservationViewModel(
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase
) : ViewModel() {
    private val _state = mutableStateOf(ParkingReservationState())
    val state: State<ParkingReservationState> = _state

    fun loadParkingArea(parkingAreaId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getParkingAreaByIdUseCase(parkingAreaId)
                .onSuccess { parkingArea ->
                    _state.value = _state.value.copy(
                        name = parkingArea.name,
                        capacity = parkingArea.capacity,
                        currentOccupancy = parkingArea.currentOccupancy,
                        openingTime = parkingArea.openingTime,
                        closingTime = parkingArea.closingTime,
                        rules = parkingArea.rules,
                        isOperative = parkingArea.isOperative,
                        isLoading = false
                    )
                }
                .onFailure { error ->
                    when(error){
                        is NoNetworkException -> _state.value = _state.value.copy(error = error)
                        else -> _state.value = _state.value.copy(error = Exception(error.message))
                    }
                    _state.value = _state.value.copy(isLoading = false)
                }
        }
    }

}