package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import es.ubu.bikeparkingapp.domain.entity.Reservation

/**
 * Representa el estado de la pantalla de reservas.
 */
data class MyTripsState(
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val reservations: List<Reservation> = emptyList(),
    val checkInReservationDialog : Boolean = false,
    val checkOutReservationDialog : Boolean = false,
    val cancelReservationDialog : Boolean = false,
    val reservationId : String? = null
)