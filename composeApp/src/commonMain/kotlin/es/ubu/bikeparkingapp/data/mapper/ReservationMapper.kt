package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.ReservationDto
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import kotlin.time.Instant

/**
 * Representa la conversión de un objeto [ReservationDto] a un objeto [Reservation].
 */
fun ReservationDto.toDomain() = Reservation(
    reservationId = reservationId,
    accountId = accountId,
    parkingAreaId = parkingAreaId,
    inTime = Instant.parse(inTime),
    outTime = Instant.parse(outTime),
    state = ReservationState.fromString(state),
    distance = distance,
    createdAt = Instant.parse(createdAt)
)

/**
 * Representa la conversión de un objeto [Reservation] a un objeto [ReservationDto].
 */
fun Reservation.toDto() = ReservationDto(
    reservationId = reservationId,
    accountId = accountId,
    parkingAreaId = parkingAreaId,
    inTime = inTime.toString(),
    outTime = outTime.toString(),
    state = state.toString(),
    distance = distance,
    createdAt = createdAt.toString()
)