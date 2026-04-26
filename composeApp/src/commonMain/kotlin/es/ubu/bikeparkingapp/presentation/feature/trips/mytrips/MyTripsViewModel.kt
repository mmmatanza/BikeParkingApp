package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import es.ubu.bikeparkingapp.domain.entity.ReservationDetail
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.usecase.reservation.CancelReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckInReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.CheckOutReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.ExtendReservationUseCase
import es.ubu.bikeparkingapp.domain.usecase.reservation.GetDetailedUserReservationsUseCase
import es.ubu.bikeparkingapp.domain.usecase.user.GetUserIdUseCase
import es.ubu.bikeparkingapp.presentation.common.util.ErrorMapper
import kotlinx.coroutines.launch
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus

/**
 * Representa el viewmodel de la pantalla de reservas.
 * @property userIdUseCase Caso de uso para obtener el ID del usuario.
 * @property getDetailedUserReservationsUseCase Caso de uso para obtener las reservas del usuario detalladas.
 * @property cancelReservationUseCase Caso de uso para cancelar una reserva.
 * @property checkInReservationUseCase Caso de uso para check-in de una reserva.
 * @property checkOutReservationUseCase Caso de uso para check-out de una reserva.
 * @property extendReservationUseCase Caso de uso para extender una reserva
 */
class MyTripsViewModel(
    private val userIdUseCase: GetUserIdUseCase,
    private val getDetailedUserReservationsUseCase: GetDetailedUserReservationsUseCase,
    private val cancelReservationUseCase: CancelReservationUseCase,
    private val checkInReservationUseCase: CheckInReservationUseCase,
    private val checkOutReservationUseCase: CheckOutReservationUseCase,
    private val extendReservationUseCase: ExtendReservationUseCase
) : ViewModel() {
    private val _state = mutableStateOf(MyTripsState())
    val state: State<MyTripsState> = _state

    fun loadTrips() {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            userIdUseCase()
                .onSuccess { accountId ->
                    getDetailedUserReservationsUseCase(accountId)
                        .onSuccess {
                            detailedReservations ->
                            // Ordenamos por el estado de reserva y luego por la fecha de entrada
                            val sortedReservations = detailedReservations.sortedWith(
                                compareBy<ReservationDetail> {
                                    when (it.reservation.state) {
                                        ReservationState.CHECKED_IN -> 1
                                        ReservationState.RESERVED -> 2
                                        else -> 3
                                    }
                                }.thenByDescending { it.reservation.inTime }
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
                    val updatedList = state.value.reservations.map { detail ->
                        if (detail.reservation.reservationId == resId) {
                            // Copiamos el detalle, y dentro la reserva con el nuevo estado
                            detail.copy(
                                reservation = detail.reservation.copy(state = ReservationState.CHECKED_IN)
                            )
                        } else {
                            detail
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
                    val updatedList = state.value.reservations.map { detail ->
                        if (detail.reservation.reservationId == resId) {
                            // Copiamos el detalle, y dentro la reserva con el nuevo estado
                            detail.copy(
                                reservation = detail.reservation.copy(state = ReservationState.CHECKED_OUT)
                            )
                        } else {
                            detail
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
                    val updatedList = state.value.reservations.map { detail ->
                        if (detail.reservation.reservationId == resId) {
                            // Copiamos el detalle, y dentro la reserva con el nuevo estado
                            detail.copy(
                                reservation = detail.reservation.copy(state = ReservationState.CANCELLED)
                            )
                        } else {
                            detail
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

    fun extendReservationDialog(reservationId: String) {
        _state.value =
            _state.value.copy(extendReservationDialog = true, reservationId = reservationId)
    }

    fun extendReservationDialogDismiss() {
        _state.value = _state.value.copy(extendReservationDialog = false, reservationId = null)
    }

    fun extendReservation() {
        if (state.value.reservationId == null) return;
        viewModelScope.launch {
            val resId = state.value.reservationId!!
            extendReservationUseCase(
                resId,
                state.value.reservations.first { it.reservation.reservationId == resId }.reservation.outTime,
                60
            )
                .onSuccess {
                    val updatedList = state.value.reservations.map { detail ->
                        if (detail.reservation.reservationId == resId) {
                            // Copiamos el detalle, y dentro la reserva con la salida actualizada
                            detail.copy(
                                reservation = detail.reservation.copy(outTime = detail.reservation.outTime.plus(60, DateTimeUnit.MINUTE))
                            )
                        } else {
                            detail
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
                        _state.value.copy(error = ErrorMapper.map(it), extendReservationDialog = false, reservationId = null)
                }
        }
    }
}