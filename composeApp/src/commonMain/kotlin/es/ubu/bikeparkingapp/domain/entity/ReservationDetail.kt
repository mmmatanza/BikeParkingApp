package es.ubu.bikeparkingapp.domain.entity

/**
 * Representa una reserva con detalles adicionales.
 * @property reservation Reserva.
 * @property parkingName Nombre del parking.
 * @property parkingAddress Dirección del parking.
 * @property parkingLatitude Latitud del parking.
 * @property parkingLongitude Longitud del parking.
 */
data class ReservationDetail(
    val reservation: Reservation,
    val parkingName: String,
    val parkingAddress: String,
    val parkingLatitude: Double,
    val parkingLongitude: Double
)