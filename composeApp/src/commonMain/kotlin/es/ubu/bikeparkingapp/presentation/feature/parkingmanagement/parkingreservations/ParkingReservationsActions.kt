package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

/**
 * Representa las acciones de la pantalla de reservas de un parking.
 * @property onBackClick Acción al hacer click en el botón de volver.
 * @property onCancelReservationClick Acción al hacer click en el botón de cancelar una reserva.
 * @property onReleaseClick Acción al hacer click en el botón de liberar una reserva.
 */
data class ParkingReservationsActions(
    val onBackClick: () -> Unit = {},
    val onCancelReservationClick: (String) -> Unit = {},
    val onReleaseClick: (String) -> Unit = {}
)