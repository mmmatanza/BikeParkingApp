package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetParkingAreaActiveReservationsUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de reservas de un parking.
 * @property getParkingReservationsUseCase Caso de uso para obtener las reservas de un parking.
 */
class ParkingReservationsViewModel(
    private val getParkingReservationsUseCase: GetParkingAreaActiveReservationsUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(ParkingReservationsState())
    val state: State<ParkingReservationsState> = _state

    fun loadReservations(parkingAreaId: String){
        _state.value = _state.value.copy(isLoading = true)
        viewModelScope.launch {
            getParkingReservationsUseCase(parkingAreaId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        isLoading = false,
                        reservations = it
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }

    fun clearState(){
        _state.value = ParkingReservationsState()
    }

    fun clearError(){
        _state.value = _state.value.copy(error = null)
    }

    fun showCancelReservationDialog(reservationId: String){
        _state.value = _state.value.copy(
            showCancelReservationDialog = true,
            reservationIdToCancel = reservationId
        )
    }

    fun dismissCancelReservationDialog(){
        _state.value = _state.value.copy(
            showCancelReservationDialog = false,
            reservationIdToCancel = null
        )
    }

    fun cancelReservation(){
        val reservationId = _state.value.reservationIdToCancel ?: return
        viewModelScope.launch {
            cancelReservationUseCase(reservationId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        reservations = _state.value.reservations.filter { it.reservationId != reservationId }
                    )
                    dismissCancelReservationDialog()
                }
                .onFailure {
                    dismissCancelReservationDialog()
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it)
                    )
                }
        }
    }
}