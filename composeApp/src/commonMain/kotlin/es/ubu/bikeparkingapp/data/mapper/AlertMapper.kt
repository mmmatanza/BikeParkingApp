package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.AlertDto
import es.ubu.bikeparkingapp.domain.entity.Alert
import es.ubu.bikeparkingapp.domain.entity.AlertType
import kotlin.time.Instant

/**
 * Convierte un objeto [AlertDto] a un objeto [Alert]
 */
fun AlertDto.toDomain(): Alert = Alert(
    alertId = alertId,
    accountId = accountId,
    parkingAreaId = parkingAreaId,
    parkingName = parkingArea?.name,
    reservationId = reservationId,
    type = AlertType.fromString(alertType),
    value = alertValue,
    customMessage = customMessage,
    isRead = isRead,
    createdAt = Instant.parse(createdAt)
)
