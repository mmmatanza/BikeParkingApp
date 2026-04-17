package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import es.ubu.bikeparkingapp.domain.entity.Reservation

/**
 * Representa el estado de la pantalla de reservas de un parking.
 * @property isLoading Indica si se está cargando la información.
 * @property reservations Lista de reservas.
 */
data class ParkingReservationsState(
    val error: Exception? = null,
    val isLoading: Boolean = false,
    val reservations: List<Reservation> = emptyList(),
    val showCancelReservationDialog: Boolean = false,
    val reservationIdToCancel: String? = null
)