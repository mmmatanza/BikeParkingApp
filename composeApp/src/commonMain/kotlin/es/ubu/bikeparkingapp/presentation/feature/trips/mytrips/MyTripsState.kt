package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

import es.ubu.bikeparkingapp.domain.entity.ReservationDetail

/**
 * Representa el estado de la pantalla de reservas.
 * @param isLoading Indica si la pantalla está cargando.
 * @param error Error que ha ocurrido.
 * @param reservations Reservas del usuario.
 * @param checkInReservationDialog Indica si se debe mostrar el diálogo de check-in.
 * @param checkOutReservationDialog Indica si se debe mostrar el diálogo de check-out.
 * @param cancelReservationDialog Indica si se debe mostrar el diálogo de cancelación.
 * @param extendReservationDialog Indica si se debe mostrar el diálogo de extensión.
 * @param reservationId ID de la reserva actual.
 */
data class MyTripsState(
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val reservations: List<ReservationDetail> = emptyList(),
    val checkInReservationDialog : Boolean = false,
    val checkOutReservationDialog : Boolean = false,
    val cancelReservationDialog : Boolean = false,
    val extendReservationDialog : Boolean = false,
    val reservationId : String? = null
)