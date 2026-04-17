package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Caso de uso para hacer check-out en una reserva.
 */
class CheckOutReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository
) : CheckOutReservationUseCase {
    override suspend fun invoke(reservationId: String): Result<Unit> = runCatching {
        // Obtener la reserva desde el repositorio
        val reservation = reservationRepository.findById(reservationId).getOrNull()
            ?: throw ReservationNotFoundException()
        if (reservation.state.canTransitionTo(ReservationState.CHECKED_OUT))
            return reservationRepository.updateState(reservationId, ReservationState.CHECKED_OUT)
        throw InvalidReservationStateException()
    }
}