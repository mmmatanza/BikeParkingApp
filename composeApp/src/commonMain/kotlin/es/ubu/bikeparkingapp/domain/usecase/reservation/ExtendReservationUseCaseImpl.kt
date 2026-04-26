package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.repository.ReservationRepository
import kotlinx.datetime.DateTimeUnit
import kotlinx.datetime.plus
import kotlin.time.Instant

/**
 * Implementación del caso de uso para extender una reserva.
 * @property reservationRepository Repositorio de reservas.
 */
class ExtendReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository
): ExtendReservationUseCase {
    override suspend fun invoke(
        reservationId: String,
        currentOutTime: Instant,
        minutes: Int
    ): Result<Unit> = runCatching{
        val newOutTime = currentOutTime.plus(minutes, DateTimeUnit.MINUTE)
        reservationRepository.extend(reservationId, newOutTime)
    }
}