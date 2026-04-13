package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa los datos de una reserva obtenidos de la base de datos.
 * @property reservationId Identificador único de la reserva
 * @property accountId Identificador único del usuario que realiza la reserva
 * @property parkingAreaId Identificador único del parking al que se reserva
 * @property inTime Fecha y hora de inicio de la reserva (el margen de entrada puede variar)
 * @property outTime Fecha y hora de finalización de la reserva
 * @property state Estado actual de la reserva
 * @property createdAt Fecha y hora de creación de la reserva
 */

@Serializable
data class ReservationDto(
    @SerialName("reservation_id") val reservationId: String? = null,
    @SerialName("account_id") val accountId: String,
    @SerialName("parking_area_id") val parkingAreaId: String,
    @SerialName("in_time") val inTime: String,
    @SerialName("out_time") val outTime: String,
    val state: String,
    @SerialName("created_at") val createdAt: String
)

enum class State {
    RESERVED, CHECKED_IN, CHECKED_OUT, CANCELLED, EXPIRED, OVERDUE;
    companion object {
        fun fromString(value: String) =
            entries.firstOrNull { it.name.equals(value, ignoreCase = true) } ?: RESERVED
    }
}