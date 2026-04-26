package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetParkingAreaActiveReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.ReleaseReservationUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de reservas de un parking.
 * @property getParkingReservationsUseCase Caso de uso para obtener las reservas de un parking.
 * @property cancelReservationUseCase Caso de uso para cancelar una reserva.
 * @property releaseReservationUseCase Caso de uso para liberar una reserva.
 */
class ParkingReservationsViewModel(
    private val getParkingReservationsUseCase: GetParkingAreaActiveReservationsUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase,
    private val releaseReservationUseCase: ReleaseReservationUseCase
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
            reservationId = reservationId
        )
    }

    fun dismissCancelReservationDialog(){
        _state.value = _state.value.copy(
            showCancelReservationDialog = false,
            reservationId = null
        )
    }

    fun showReleaseReservationDialog(reservationId: String){
        _state.value = _state.value.copy(
            showReleaseReservationDialog = true,
            reservationId = reservationId
        )
    }

    fun dismissReleaseReservationDialog(){
        _state.value = _state.value.copy(
            showReleaseReservationDialog = false,
            reservationId = null
        )
    }

    fun cancelReservation(){
        val reservationId = _state.value.reservationId ?: return
        viewModelScope.launch {
            cancelReservationUseCase(reservationId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        reservations = _state.value.reservations.filter { it.reservationId != reservationId },
                        showCancelReservationDialog = false,
                        reservationId = null
                    )
                }
                .onFailure {
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(it),
                        showCancelReservationDialog = false,
                        reservationId = null
                    )
                }
        }
    }

    fun releaseReservation(){
        val reservationId = _state.value.reservationId ?: return
        viewModelScope.launch {
            releaseReservationUseCase(reservationId)
                .onSuccess {
                    _state.value = _state.value.copy(
                        reservations = _state.value.reservations.filter { it.reservationId != reservationId },
                        showReleaseReservationDialog = false,
                        reservationId = null
                    )
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(
                        error = ErrorMapper.map(error),
                        showReleaseReservationDialog = false,
                        reservationId = null
                    )
                }

        }
    }
}