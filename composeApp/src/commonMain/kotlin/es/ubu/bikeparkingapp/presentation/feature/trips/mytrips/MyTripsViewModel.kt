package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetUserReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch

/**
 * Representa el viewmodel de la pantalla de reservas.
 */
class MyTripsViewModel(
    private val userIdUseCase: GetUserIdUseCase,
    private val getUserReservationsUseCase: GetUserReservationsUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase,
    private val checkInReservationUseCase: CheckInReservationUseCase,
    private val checkOutReservationUseCase: CheckOutReservationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(MyTripsState())
    val state: State<MyTripsState> = _state

    fun loadTrips() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userIdUseCase()
                .onSuccess { accountId ->
                    getUserReservationsUseCase(accountId)
                        .onSuccess { reservations ->
                            // Ordenamos las reservas
                            val sortedReservations = reservations.sortedWith(
                                compareBy<Reservation> {
                                    // Primero por prioridad de estado
                                    when (it.state) {
                                        ReservationState.CHECKED_IN -> 1
                                        ReservationState.RESERVED -> 2
                                        else -> 3
                                    }
                                }.thenByDescending { it.inTime } // Y luego por fecha
                            )

                            _state.value = _state.value.copy(
                                isLoading = false,
                                reservations = sortedReservations
                            )
                        }
                        .onFailure {
                            _state.value = _state.value.copy(isLoading = false, error = ErrorMapper.map(it))
                        }
                }
                .onFailure {
                    _state.value = _state.value.copy(isLoading = false, error = ErrorMapper.map(it))
                }
        }
    }

    fun clearError() {
        _state.value = _state.value.copy(error = null)
    }

    fun clearState() {
        _state.value = MyTripsState()
    }

    fun checkInReservationDialog(reservationId: String) {
        _state.value =
            _state.value.copy(checkInReservationDialog = true, reservationId = reservationId)
    }

    fun checkInReservationDialogDismiss() {
        _state.value = _state.value.copy(checkInReservationDialog = false, reservationId = null)
    }

    fun checkInReservation() {
        if (state.value.reservationId == null) return;
        viewModelScope.launch {
            val resId = state.value.reservationId!!
            checkInReservationUseCase(resId)
                .onSuccess {
                    val updatedList = state.value.reservations.map { reservation ->
                        if (reservation.reservationId == resId) {
                            reservation.copy(state = ReservationState.CHECKED_IN)
                        } else {
                            reservation
                        }
                    }
                    _state.value = _state.value.copy(
                        reservations = updatedList,
                        checkInReservationDialog = false,
                        reservationId = null
                    )
                }
                .onFailure {
                    _state.value =
                        _state.value.copy(error = ErrorMapper.map(it), checkInReservationDialog = false, reservationId = null)
                }
        }
    }

    fun checkOutReservationDialog(reservationId: String) {
        _state.value =
            _state.value.copy(checkOutReservationDialog = true, reservationId = reservationId)
    }

    fun checkOutReservationDialogDismiss() {
        _state.value = _state.value.copy(checkOutReservationDialog = false, reservationId = null)
    }

    fun checkOutReservation() {
        if (state.value.reservationId == null) return;
        viewModelScope.launch {
            val resId = state.value.reservationId!!
            checkOutReservationUseCase(resId)
                .onSuccess {
                    val updatedList = state.value.reservations.map { reservation ->
                        if (reservation.reservationId == resId) {
                            reservation.copy(state = ReservationState.CHECKED_OUT)
                        } else {
                            reservation
                        }
                    }
                    _state.value = _state.value.copy(
                        reservations = updatedList,
                        checkOutReservationDialog = false,
                        reservationId = null
                    )
                }
                .onFailure {
                    _state.value =
                        _state.value.copy(error = ErrorMapper.map(it), checkOutReservationDialog = false, reservationId = null)
                }
        }
    }

    fun cancelReservationDialog(reservationId: String) {
        _state.value =
            _state.value.copy(cancelReservationDialog = true, reservationId = reservationId)
    }

    fun cancelReservationDialogDismiss() {
        _state.value = _state.value.copy(cancelReservationDialog = false, reservationId = null)
    }

    fun cancelReservation() {
        if (state.value.reservationId == null) return;
        viewModelScope.launch {
            val resId = state.value.reservationId!!
            cancelReservationUseCase(resId)
                .onSuccess {
                    val updatedList = state.value.reservations.map { reservation ->
                        if (reservation.reservationId == resId) {
                            reservation.copy(state = ReservationState.CANCELLED)
                        } else {
                            reservation
                        }
                    }
                    _state.value = _state.value.copy(
                        reservations = updatedList,
                        cancelReservationDialog = false,
                        reservationId = null
                    )
                }
                .onFailure {
                    _state.value =
                        _state.value.copy(error = ErrorMapper.map(it), cancelReservationDialog = false, reservationId = null)
                }
        }
    }
}