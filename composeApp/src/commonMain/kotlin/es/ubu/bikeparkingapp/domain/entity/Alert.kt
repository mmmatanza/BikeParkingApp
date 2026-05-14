package es.ubu.bikeparkingapp.domain.entity

import kotlinx.serialization.Serializable
import kotlin.time.Instant

/**
 * Clase que representa una alerta.
 */
@Serializable
data class Alert(
    val alertId: String,
    val accountId: String,
    val parkingAreaId: String?,
    val reservationId: String?,
    val type: AlertType,
    val value: Double?,
    val isRead: Boolean,
    val createdAt: Instant
)

@Serializable
enum class AlertType {
    OCCUPANCY_LIMIT,
    SUSPICIOUS_RESERVATION,
    PARKING_NOTIFICATION;

    companion object {
        fun fromString(value: String): AlertType = entries.find { it.name == value } ?: OCCUPANCY_LIMIT
    }
}
