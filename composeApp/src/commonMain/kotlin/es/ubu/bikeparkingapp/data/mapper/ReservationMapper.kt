package es.ubu.bikeparkingapp.data.mapper

import es.ubu.bikeparkingapp.data.dto.ReservationDto
import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState

/**
 * Representa la conversión de un objeto [ReservationDto] a un objeto [Reservation].
 */
fun ReservationDto.toDomain() = Reservation(
    reservationId = reservationId,
    accountId = accountId,
    parkingAreaId = parkingAreaId,
    inTime = inTime,
    outTime = outTime,
    slots = slots,
    state = ReservationState.fromString(state),
    createdAt = createdAt
)

/**
 * Representa la conversión de un objeto [Reservation] a un objeto [ReservationDto].
 */
fun Reservation.toDto() = ReservationDto(
    reservationId = reservationId,
    accountId = accountId,
    parkingAreaId = parkingAreaId,
    inTime = inTime,
    outTime = outTime,
    slots = slots,
    state = state.toString(),
    createdAt = createdAt
)