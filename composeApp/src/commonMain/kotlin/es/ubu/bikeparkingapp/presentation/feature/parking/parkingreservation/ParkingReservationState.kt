package es.ubu.bikeparkingapp.presentation.feature.parking.parkingreservation

import es.ubu.bikeparkingapp.domain.entity.ParkingArea

/**
 * Representa el estado de la pantalla de reserva de plaza.
 * @property isLoading Indica si se está cargando la información.
 * @property error Error ocurrido.
 * @property name Nombre del parking.
 * @property capacity Capacidad máxima del parking.
 * @property currentOccupancy Ocupación actual del parking.
 * @property openingTime Horario de apertura del parking.
 * @property closingTime Horario de cierre del parking.
 * @property rules Lista de reglas del parking.
 * @property isOperative Indica si el parking está operativo.
 */
data class ParkingReservationState(
    val isLoading: Boolean = false,
    val error: Exception? = null,
    val name: String = "",
    val capacity: Int = 0,
    val currentOccupancy: Int = 0,
    val openingTime: String = "",
    val closingTime: String = "",
    val rules: List<String> = emptyList(),
    val isOperative: Boolean = false
)