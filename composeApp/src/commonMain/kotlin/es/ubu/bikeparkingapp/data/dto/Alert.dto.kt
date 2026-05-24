package es.ubu.bikeparkingapp.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Representa los datos de una alerta obtenida de la base de datos.
 */
@Serializable
data class AlertDto(
    @SerialName("alert_id") val alertId: String,
    @SerialName("account_id") val accountId: String,
    @SerialName("parking_area_id") val parkingAreaId: String? = null,
    @SerialName("reservation_id") val reservationId: String? = null,
    @SerialName("alert_type") val alertType: String,
    @SerialName("alert_value") val alertValue: Double? = null,
    @SerialName("custom_message") val customMessage: String? = null,
    @SerialName("is_read") val isRead: Boolean,
    @SerialName("created_at") val createdAt: String,
    @SerialName("parkingareas") val parkingArea: ParkingNameDto? = null
)

/**
 * DTO auxiliar para obtener el nombre del parking mediante un join.
 */
@Serializable
data class ParkingNameDto(
    val name: String
)
