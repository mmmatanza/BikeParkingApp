package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

import es.ubu.bikeparkingapp.domain.entity.Reservation

/**
 * Representa el estado de la pantalla de reservas de un parking.
 * @property error Error ocurrido.
 * @property isLoading Indica si se está cargando la información.
 * @property reservations Lista de reservas.
 * @property showCancelReservationDialog Indica si se debe mostrar el diálogo de cancelación de reserva.
 * @property showReleaseReservationDialog Indica si se debe mostrar el diálogo de liberación de reserva.
 * @property reservationId Id de la reserva seleccionada para la acción.
 */
data class ParkingReservationsState(
    val error: Exception? = null,
    val isLoading: Boolean = false,
    val reservations: List<Reservation> = emptyList(),
    val showCancelReservationDialog: Boolean = false,
    val showReleaseReservationDialog: Boolean = false,
    val reservationId: String? = null
)