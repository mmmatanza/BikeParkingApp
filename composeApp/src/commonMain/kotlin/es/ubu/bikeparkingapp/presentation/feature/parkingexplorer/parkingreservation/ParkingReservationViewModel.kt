package es.ubu.bikeparkingapp.presentation.feature.parkingexplorer.parkingreservation

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.parking.GetParkingAreaByIdUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.AddReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewModel para la pantalla de reserva de plaza.
 * @property getParkingAreaByIdUseCase Caso de uso para obtener un parking por su id.
 * @property addReservationUseCase Caso de uso para añadir una reserva.
 * @property getUserIdUseCase Caso de uso para obtener el id del usuario actual.
 */
class ParkingReservationViewModel(
    private val getParkingAreaByIdUseCase: GetParkingAreaByIdUseCase,
    private val addReservationUseCase: AddReservationUseCase,
    private val getUserIdUseCase: GetUserIdUseCase
) : ViewModel() {
    private val _state = mutableStateOf(ParkingReservationState())
    val state: State<ParkingReservationState> = _state

    fun loadParkingArea(parkingAreaId: String) {
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getParkingAreaByIdUseCase(parkingAreaId)
                .onSuccess { parkingArea ->
                    _state.value = _state.value.copy(
                        parkingArea = parkingArea,
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
    }

    fun availableParking(): Boolean {
        val area = _state.value.parkingArea ?: return false
        val hasSpace = (area.capacity - area.currentOccupancy) > 0
        return hasSpace && area.isOperative && area.isActive
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearState() {
        _state.value = ParkingReservationState()
    }

    fun confirmReservationDialog() {
        _state.value = _state.value.copy(confirmReservationDialog = true)
    }

    fun clearConfirmReservationDialog() {
        _state.value = _state.value.copy(confirmReservationDialog = false)
    }

    fun addReservation() {
        clearConfirmReservationDialog()
        val parkingId = _state.value.parkingArea?.parkingAreaId ?: return
        viewModelScope.launch {
            getUserIdUseCase().onSuccess { accountId ->
                addReservationUseCase(
                    parkingAreaId = parkingId,
                    accountId = accountId
                ).onSuccess {
                    _state.value = _state.value.copy(successfulReservation = true)
                }.onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
            }
        }
    }
}