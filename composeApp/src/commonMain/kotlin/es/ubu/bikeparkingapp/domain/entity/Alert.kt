package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Clase que representa una alerta.
 * @param alertId Identificador único de la alerta.
 * @param accountId Identificador del usuario asociado a la alerta.
 * @param parkingAreaId Identificador del área de parking asociada a la alerta, si aplica.
 * @param parkingName Nombre del área de parking asociada a la alerta, si aplica.
 * @param reservationId Identificador de la reserva asociada a la alerta, si aplica.
 * @param type Tipo de alerta.
 * @param value Valor asociado a la alerta, si aplica.
 * @param customMessage Mensaje personalizado asociado a la alerta, si aplica.
 * @param isRead Indica si la alerta ha sido leída o no.
 * @param createdAt Fecha y hora en la que se creó la alerta.
 */
@Serializable
data class Alert(
    val alertId: String,
    val accountId: String,
    val parkingAreaId: String?,
    val parkingName: String? = null,
    val reservationId: String?,
    val type: AlertType,
    val value: Double?,
    val customMessage: String?,
    val isRead: Boolean,
    val createdAt: Instant
)

@Serializable
enum class AlertType {
    OCCUPANCY_LIMIT,
    PREDICTED_OCCUPANCY,
    SUSPICIOUS_RESERVATION,
    PARKING_NOTIFICATION,
    RECURRENT_EXPIRED,
    RECURRENT_CANCELLATIONS,
    RECURRENT_OVERSTAY,
    SUSPICIOUS_NEW_ACCOUNT,
    UNUSUAL_BOOKING_FREQUENCY,
    UNUSUAL_BOOKING_HOUR,
    UNUSUAL_BOOKING_WEEKDAY,
    ABNORMAL_BOOKING_PATTERN,
    RESERVATION_CANCELLED;

    companion object {
        fun fromString(value: String): AlertType = entries.find { it.name == value } ?: PARKING_NOTIFICATION
    }
}
