package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Caso de uso para hacer check-in en una reserva.
 */
class CheckInReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository
) : CheckInReservationUseCase {
    override suspend fun invoke(reservationId: String): Result<Unit> = runCatching {
        // Obtener la reserva desde el repositorio
        val reservation = reservationRepository.findById(reservationId).getOrNull()
            ?: throw ReservationNotFoundException()
        if (reservation.state.canTransitionTo(ReservationState.CHECKED_IN))
            return reservationRepository.updateState(reservationId, ReservationState.CHECKED_IN)
        throw InvalidReservationStateException()

    }
}