package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.Reservation
import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.AccountHasActiveReservationException
import es.ubu.bikeparkingapp.domain.exception.ParkingClosingSoonException
import es.ubu.bikeparkingapp.domain.exception.ParkingHasNoFreeSpotsException
import es.ubu.bikeparkingapp.domain.exception.ParkingIsClosedException
import es.ubu.bikeparkingapp.domain.repository.ParkingAreaRepository
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import kotlinx.datetime.LocalTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.atTime
import kotlinx.datetime.toInstant
import kotlinx.datetime.toLocalDateTime
import kotlin.time.Clock
import kotlin.time.Duration.Companion.minutes

/**
 * Caso de uso para añadir una reserva.
 */
class AddReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository,
    private val parkingAreaRepository: ParkingAreaRepository
) : AddReservationUseCase {

    // Implementación del caso de uso para añadir una reserva
    // Comprobar que el parking no cierra antes del fin de reserva

    override suspend fun invoke(
        parkingAreaId: String,
        accountId: String
    ): Result<Unit> = runCatching {

        // Obtener el parking
        val parkingArea = parkingAreaRepository.getParkingAreaById(parkingAreaId).getOrNull()
            ?: throw Exception("Parking area not found")

        val reservations = reservationRepository.countUserActiveReservations(accountId)
        if (reservations>0) throw AccountHasActiveReservationException()

        // Obtener la hora actual
        val now = Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault())
        val nowInstant = now.toInstant(TimeZone.currentSystemDefault())

        // Obtener el horario de apertura y cierre
        val start = parseLocalTime(parkingArea.openingTime)
        val end = parseLocalTime(parkingArea.closingTime)
        // Detectamos si el parking es 24 horas
        val is24Hours = start == LocalTime(0, 0) && end == LocalTime(23, 59)

        // Comprobar que el parking está abierto y hay plazas
        if (!parkingArea.openDays.contains(now.dayOfWeek) || (!is24Hours && now.time !in start..end))
            throw ParkingIsClosedException()
        if (parkingArea.capacity - parkingArea.currentOccupancy <= 0)
            throw ParkingHasNoFreeSpotsException()

        // La reserva no llega al tiempo mínimo antes del cierre
        val minutesUntilClosing = if (is24Hours) Int.MAX_VALUE.toLong()
        else (now.date.atTime(end).toInstant(TimeZone.currentSystemDefault()) - nowInstant).inWholeMinutes
        if (!is24Hours && minutesUntilClosing < MINIMUM_DURATION + CORTESY_MINUTES)
            throw ParkingClosingSoonException()

        val inTime = nowInstant.plus(CORTESY_MINUTES.minutes)
        val outTime = inTime.plus(RESERVATION_DURATION.minutes)

        try {
            reservationRepository.save(
                Reservation(
                    reservationId = null,
                    parkingAreaId = parkingAreaId,
                    accountId = accountId,
                    inTime = inTime,
                    outTime = outTime,
                    state = ReservationState.RESERVED,
                    createdAt = nowInstant
                )
            )
        } catch (e: Exception) {
            // En caso de que el trigger devuelva una excepción
            if (e.message?.contains("ParkingHasNoFreeSpotsException") == true) {
                throw ParkingHasNoFreeSpotsException()
            }
            throw e
        }
    }

    private fun parseLocalTime(time: String): LocalTime {
        val (hour, min) = time.split(":").map { it.toInt() }
        return LocalTime(hour, min)
    }

    companion object {
        // El tiempo de cortesía para llegar al aparcamiento
        const val CORTESY_MINUTES = 20
        // Tiempo de la estancia
        const val RESERVATION_DURATION = 60
        // Tiempo mínimo de estancia
        const val MINIMUM_DURATION = 20
    }
}