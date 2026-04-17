package es.ubu.bikeparkingapp.domain.usecase.reservation

import es.ubu.bikeparkingapp.domain.entity.ReservationState
import es.ubu.bikeparkingapp.domain.exception.InvalidReservationStateException
import es.ubu.bikeparkingapp.domain.exception.ReservationNotFoundException
import es.ubu.bikeparkingapp.domain.repository.ReservationRepository

/**
 * Implementación del caso de uso para cancelar una reserva.
 */
class CancelReservationUseCaseImpl(
    private val reservationRepository: ReservationRepository
): CancelReservationUseCase {
    override suspend fun invoke(reservationId: String): Result<Unit> = runCatching {
        val reservation = reservationRepository.findById(reservationId).getOrNull()
            ?: throw ReservationNotFoundException()
        // Comprobamos que la reserva se puede cancelar
        if(reservation.state.canTransitionTo(ReservationState.CANCELLED))
            return reservationRepository.updateState(reservationId, ReservationState.CANCELLED)
        throw InvalidReservationStateException()
    }
}