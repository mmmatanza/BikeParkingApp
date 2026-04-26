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
 * @property reservationRepository Repositorio de reservas.
 * @property parkingAreaRepository Repositorio de parkings.
 */
class AddReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository,
    private val parkingAreaRepository: ParkingAreaRepository
) : AddReservationUseCase {

    override suspend fun invoke(
        parkingAreaId: String,
        accountId: String
    ): Result<Unit> = runCatching {

        // Validamos que el usuario no tenga una reserva activa
        val reservations = reservationRepository.findActiveReservationByAccountId(accountId).getOrNull()
        if(!reservations.isNullOrEmpty()) throw AccountHasActiveReservationException()

        // Obtenemos el parking
        val parkingArea = parkingAreaRepository.getParkingAreaById(parkingAreaId).getOrNull()
            ?: throw Exception("Parking area not found")

        // Definimos la zona horaria del parking
        val parkingTz = TimeZone.of(parkingArea.timezoneId)

        // Obtenemos el "ahora" del parking
        val nowInstant = Clock.System.now()
        val nowInParking = nowInstant.toLocalDateTime(parkingTz)

        // Validamos si está abierto (usando la hora del parking)
        val start = LocalTime.parse(parkingArea.openingTime)
        val end = LocalTime.parse(parkingArea.closingTime)
        val is24Hours = start == LocalTime(0, 0) && end == LocalTime(23, 59, 59)

        if (!parkingArea.openDays.contains(nowInParking.dayOfWeek) ||
            (!is24Hours && nowInParking.time !in start..end)) {
            throw ParkingIsClosedException()
        }

        if (parkingArea.capacity - parkingArea.currentOccupancy <= 0)
            throw ParkingHasNoFreeSpotsException()

        // Calculamos los minutos hasta el cierre
        if (!is24Hours) {
            // Creamos el instante de cierre en la zona del parking
            val closingInstant = nowInParking.date.atTime(end).toInstant(parkingTz)
            val minutesUntilClosing = (closingInstant - nowInstant).inWholeMinutes

            if (minutesUntilClosing < RESERVATION_DURATION + CORTESY_MINUTES)
                throw ParkingClosingSoonException()
        }

        val inTime = nowInstant.plus(CORTESY_MINUTES.minutes)
        val outTime = inTime.plus(RESERVATION_DURATION.minutes)

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
    }

    companion object {
        // El tiempo de cortesía para llegar al aparcamiento
        const val CORTESY_MINUTES = 20

        // Tiempo de la estancia
        const val RESERVATION_DURATION = 60
    }
}