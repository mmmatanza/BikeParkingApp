package es.ubu.bikeparkingapp.presentation.feature.trips.mytrips

/**
 * Representa las acciones de la pantalla de reservas
 * @property onBackClick Acción al hacer click en el botón de volver.
 * @property onCancelReservationClick Acción al hacer click en el botón de cancelar una reserva.
 * @property onCheckInClick Acción al hacer click en el botón de check-in.
 * @property onCheckOutClick Acción al hacer click en el botón de check-out.
 */
data class MyTripsActions(
    val onBackClick: () -> Unit = {},
    val onCancelReservationClick: (String) -> Unit = {},
    val onCheckInClick: (String) -> Unit = {},
    val onCheckOutClick: (String) -> Unit = {}
)