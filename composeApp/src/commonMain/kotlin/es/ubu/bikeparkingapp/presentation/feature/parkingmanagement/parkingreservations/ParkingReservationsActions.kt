package es.ubu.bikeparkingapp.presentation.feature.parkingmanagement.parkingreservations

/**
 * Representa las acciones de la pantalla de reservas de un parking.
 * @property onBackClick Acción al hacer click en el botón de volver.
 */
data class ParkingReservationsActions(
    val onBackClick: () -> Unit = {},
    val onCancelReservationClick: (String) -> Unit = {}
)